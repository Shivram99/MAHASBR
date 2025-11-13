package com.mahasbr.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.dto.MstRegistryDetailsPagesDTO;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.entity.MstRegistryFailedEntity;
import com.mahasbr.mapper.MstRegistryDetailsMapper;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;
import com.mahasbr.repository.MstRegistryFailedRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistryApiService {

	private final WebClient webClient;
	private final MultiAuthService authService;
	private final MstRegistryDetailsPageRepository repository;
	private final ApiLogService apiLogService;

	private final MstRegistryFailedRepository mstRegistryFailedRepository;

	private final MstRegistryDetailsMapper mstRegistryDetailsMapper;

	private final ObjectMapper objectMapper;

	private static final Logger logger = LoggerFactory.getLogger(RegistryApiService.class);

	private static final SecureRandom RANDOM = new SecureRandom();

	private static final String CHARITY_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetSBRShopData";
	private static final String DISH_URL = "https://sbrlmsapi.mahaonline.gov.in/api/SBR/GetSBRShopData";
	private static final String LABOUR_URL = "https://sbrlmsapi.mahaonline.gov.in/api/SBR/getSBRFactoryData";
	private static final String COOP_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetCoOperativeData";
	private static final String KVIB_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetKVIBData";
	private static final String MCGM_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetMCGMData";
	private static final String MSME_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetMSMEData";
	private static final String MCA_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetMCAData";
	private static final String GSTN_URL = "https://sbrlmsapi.mahaitgov.in/api/SBR/GetGSTNData";

	public static final Map<Integer, String> AUTHORITY_MAP = Map.ofEntries(Map.entry(21, "CHARITY"),
			Map.entry(22, "DISH"), Map.entry(23, "LABOUR"), Map.entry(24, "CO-OP"), Map.entry(25, "KVIB"),
			Map.entry(26, "MCGM"), Map.entry(27, "MSME"), Map.entry(28, "MCA"), Map.entry(29, "GSTN"));

	@Async
	@Retryable(value = { RuntimeException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000))
	public CompletableFuture<Void> fetchAndSave(ApiAuthConfig config, String apiName, String url, String registrory) {
		try {

			String fromDate = "2025-01-22";
			String toDate = "2025-02-22";
			// 1️⃣ Get token
			String token = authService.getToken(config);

			// 2️⃣ Prepare headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(token);

			// 3️⃣ Prepare request body
			Map<String, String> requestBody = new HashMap<>();
			requestBody.put("FromDate", fromDate);
			requestBody.put("ToDate", toDate);

			String jsonBody = objectMapper.writeValueAsString(requestBody);

			HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

			// 4️⃣ Execute API call
			ResponseEntity<String> responseEntity = new RestTemplate().exchange(url, HttpMethod.POST, entity,
					String.class);

			String response = responseEntity.getBody();

			if (response == null || response.isEmpty()) {
				apiLogService.log(apiName, url, "FAILURE", "No data received");
				return CompletableFuture.completedFuture(null);
			}

			// 5️⃣ Parse JSON into DTOs
			List<MstRegistryDetailsPagesDTO> dtos = objectMapper.readValue(response,
					new TypeReference<List<MstRegistryDetailsPagesDTO>>() {
					});

			int successCount = 0;
			int failedCount = 0;

			for (MstRegistryDetailsPagesDTO dto : dtos) {
				try {
					processSingleRecord(dto, response, apiName, url, registrory);
					successCount++;
				} catch (Exception ex) {
					failedCount++;
					saveFailedRecord(dto, response, ex.getMessage(), apiName, url);
				}
			}

			apiLogService.log(apiName, url, "SUCCESS",
					String.format("Processed %d records. Saved=%d, Failed=%d", dtos.size(), successCount, failedCount));

		} catch (Exception e) {
			apiLogService.log(apiName, url, "FAILURE", e.getMessage());
			throw new RuntimeException("Error in fetchAndSave for API: " + apiName, e);
		}

		return CompletableFuture.completedFuture(null);
	}

	// Convenience methods
	public CompletableFuture<Void> fetchCharity() {
		return fetchAndSave(ApiAuthConfig.CHARITY, "Charity Commissioner", CHARITY_URL, "CHARITY");
	}

	public CompletableFuture<Void> fetchDish() {
		return fetchAndSave(ApiAuthConfig.DISH, "DISH", DISH_URL, "DISH");
	}

	public CompletableFuture<Void> fetchLabour() {
		return fetchAndSave(ApiAuthConfig.LABOUR, "Labour", LABOUR_URL, "LABOUR");
	}

	public CompletableFuture<Void> fetchCoop() {
		return fetchAndSave(ApiAuthConfig.COOP, "Co-Op Societies", COOP_URL, "CO-OP");
	}

	public CompletableFuture<Void> fetchKvib() {
		return fetchAndSave(ApiAuthConfig.KVIB, "KVIB", KVIB_URL, "KVIB");
	}

	public CompletableFuture<Void> fetchMcgm() {
		return fetchAndSave(ApiAuthConfig.MCGM, "MCGM", MCGM_URL, "MCGM");
	}

	public CompletableFuture<Void> fetchMsme() {
		return fetchAndSave(ApiAuthConfig.MSME, "MSME", MSME_URL, "MSME");
	}

	public CompletableFuture<Void> fetchMca() {
		return fetchAndSave(ApiAuthConfig.MCA, "MCA", MCA_URL, "MCA");
	}

	public CompletableFuture<Void> fetchGstn() {
		return fetchAndSave(ApiAuthConfig.GSTN, "GSTN", GSTN_URL, "GSTN");
	}

	public CompletableFuture<Void> fetchAllApis() {
		return CompletableFuture.allOf(
//				fetchCharity()
				fetchDish()
//                fetchLabour(),
//                fetchCoop(),
//                fetchKvib(),
//                fetchMcgm(),
//                fetchMsme(),
//                fetchMca(),
//                fetchGstn()
		);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void processSingleRecord(MstRegistryDetailsPagesDTO dto, String rawData, String apiName, String url,
			String registory) {
		// 1. Map DTO → Entity
		MstRegistryDetailsPageEntity entity = mstRegistryDetailsMapper.dtoToEntity(dto);

		// 2. Enrich with additional fields
		entity.setRegUserId(2);

		// 4. Generate Location Code (example: state + district + taluka + village)
		String locationCode = generateLocationCode(dto.getDistrict(), dto.getTaluka(), dto.getTownVillage());
		// 4. Check Location Code
		if ("NA".equals(locationCode)) {

			try {
				saveFailedRecord(dto, objectMapper.writeValueAsString(dto), "Missing location code", apiName, url);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				saveFailedRecord(dto, dto.toString(), "Missing location code", apiName, url);
			}
			return;
		}
		entity.setBrnNo(generateBrn());
		entity.setRegUserId(getKeyByValue(registory));
		entity.setLocationCode(locationCode);

		// 5. Save entity
		repository.save(entity);
	}

	/**
	 * Save failed records for analysis
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveFailedRecord(MstRegistryDetailsPagesDTO dto, String rawData, String reason, String apiName,
			String apiUrl) {
		try {
			MstRegistryFailedEntity failed = new MstRegistryFailedEntity();
			failed.setRawData(rawData);
			failed.setErrorMessage(reason);
			failed.setApiName(apiName);
			failed.setApiUrl(apiUrl);

			// Set location-related info for easier debugging
			failed.setDistrictName(dto.getDistrict());
			failed.setTalukaName(dto.getTaluka());
			failed.setVillageName(dto.getTownVillage());

			failed.setCreatedAt(LocalDateTime.now());

			mstRegistryFailedRepository.save(failed);
			logger.warn("⚠️ Saved failed record for BRN: {} | Reason: {}", dto.getBrnNo(), reason);
		} catch (Exception ex) {
			logger.error("❌ Could not save failed record. Reason: {}", ex.getMessage(), ex);
		}
	}

	/**
	 * location code generator
	 */
	private String generateLocationCode(String district, String taluka, String village) {
		if (district == null || taluka == null || village == null) {
			return "NA";
		}
		// Example format: DISTRICT_TALUKA_VILLAGE (you can change as per requirement)
		return district.substring(0, 3).toUpperCase() + taluka.substring(0, 3).toUpperCase()
				+ village.substring(0, 3).toUpperCase();
	}

	/**
	 * BRN number code generator
	 */
	public String generateBrn() {
		long randomNumber = (long) (RANDOM.nextDouble() * 1_000_000_0000L); // 10-digit random
		String ePart = String.format("%010d", randomNumber); // pad leading zeros
		return "27" + "0000" + ePart;
	}

	public static Integer getKeyByValue(String value) {
		return AUTHORITY_MAP.entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(value)) // case
																											// insensitive
				.map(Map.Entry::getKey).findFirst().orElse(null); // return null if not found
	}
}
