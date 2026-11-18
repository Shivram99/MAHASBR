package com.mahasbr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.dto.MstRegistryDetailsPagesDTO;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.entity.RegistryApiAuthConfigEntity;
import com.mahasbr.repository.RegistryApiAuthConfigRepository;
import com.mahasbr.util.RegistryRecordKey;
import com.mahasbr.util.RegistryRecordSanitizer;
import com.sun.net.httpserver.HttpServer;

/** Uses a record from the sample response MCGM sent, to prove it is read and stored correctly. */
class McgmSampleResponseTest {

	private static final String RECORD = """
			{"siNo":"0820165390","nameOfEstablishmentOrOwner":"KOLI BAKERS","houseNo":101,"streetName":"10TH",
			 "locality":"COLABA","pinCode":400001,"telephoneMobNumber":9555555555,"emailAddress":"koli@gmail.com",
			 "PANNumber":"","descriptionOfMajorActivity":"BEKARY SHOP","yearOfStartOfOperation":2026,
			 "totalNumberOfPersonsWorking":"0026","registrationStatus":"ISSUED","wardNumber":"A Ward",
			 "nameOfAct":"Maharashtra Shops and Establishments (Regulation of Employment and Condition of Service) Act, 2017",
			 "dateOfRegistration":"2026-04-22"}""";

	// Same setup as the application's mapper (Spring Boot registers the date module).
	private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

	private RegistrySyncService service() {
		return new RegistrySyncService(mock(RegistryApiAuthConfigRepository.class), mock(RegistryApiClient.class),
				mock(RegistryRecordUpsertService.class), mock(ApiLogService.class), mapper, "Asia/Kolkata", 7);
	}

	@Test
	void sampleRecordIsReadCorrectly() throws Exception {
		MstRegistryDetailsPagesDTO dto = service().parseLenient(mapper.readTree(RECORD));

		assertEquals("0820165390", dto.getActAuthorityRegistrationNumbers());   // siNo
		assertEquals("KOLI BAKERS", dto.getNameOfEstablishmentOrOwner());
		assertEquals("101", dto.getHouseNo());                                   // number sent, text stored
		assertEquals(400001, dto.getPinCode());
		assertEquals("9555555555", dto.getTelephoneMobNumber());
		assertEquals("2026", dto.getYearOfStartOfOperation());
		assertEquals(26, dto.getTotalNumberOfPersonsWorking());                  // "0026"
		assertEquals(LocalDate.of(2026, 4, 22), dto.getDateOfRegistration());
		assertEquals("A Ward", dto.getWardNumber());
		assertEquals("", dto.getPanNumber() == null ? "" : dto.getPanNumber());  // "" for PANNumber
	}

	@Test
	void missingAndEmptyFieldsEndUpBlankInTheDatabaseRow() throws Exception {
		MstRegistryDetailsPagesDTO dto = service().parseLenient(mapper.readTree(RECORD));
		MstRegistryDetailsPageEntity entity = new com.mahasbr.mapper.MstRegistryDetailsMapperImpl().dtoToEntity(dto);
		RegistryRecordSanitizer.sanitize(entity);

		assertNull(entity.getPanNumber());   // "" -> blank
		assertNull(entity.getDistrict());    // not sent -> blank
		assertNull(entity.getTaluka());
		assertEquals("0820165390", entity.getActAuthorityRegistrationNumbers());
	}

	@Test
	void sameSiNoGivesTheSameKeySoARepeatUpdatesInsteadOfDuplicating() throws Exception {
		MstRegistryDetailsPagesDTO first = service().parseLenient(mapper.readTree(RECORD));
		MstRegistryDetailsPagesDTO again = service()
				.parseLenient(mapper.readTree(RECORD.replace("KOLI BAKERS", "KOLI BAKERS & SWEETS")));

		assertEquals(RegistryRecordKey.of(first), RegistryRecordKey.of(again));
	}

	@Test
	void recordsWrappedInResponseAreFound() throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/x", exchange -> {
			byte[] reply = ("{\"Response\":[" + RECORD + "," + RECORD + "]}").getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", "application/json");
			exchange.sendResponseHeaders(200, reply.length);
			exchange.getResponseBody().write(reply);
			exchange.close();
		});
		server.start();
		try {
			RegistryApiAuthConfigEntity config = new RegistryApiAuthConfigEntity();
			config.setServiceName("MCGM");
			config.setAuthType("BASIC");
			config.setUsername("u");
			config.setPassword("p");
			config.setApiData("http://127.0.0.1:" + server.getAddress().getPort() + "/x");
			config.setApiDataMethod("POST");

			List<JsonNode> records = new RegistryApiClient(new RestTemplateBuilder(), mapper).fetchWindow(config, null,
					LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31));

			assertEquals(2, records.size());
		} finally {
			server.stop(0);
		}
	}
}
