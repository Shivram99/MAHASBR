package com.mahasbr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.entity.RegistryApiAuthConfigEntity;
import com.sun.net.httpserver.HttpServer;

/** Checks the exact request sent to a Basic-auth API such as MCGM, against a local test server. */
class RegistryApiClientBasicAuthTest {

	private HttpServer server;
	private final AtomicReference<String> authorization = new AtomicReference<>();
	private final AtomicReference<String> body = new AtomicReference<>();
	private final AtomicReference<String> method = new AtomicReference<>();

	@BeforeEach
	void start() throws Exception {
		server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/RESTAdapter/Api/SharingShop", exchange -> {
			method.set(exchange.getRequestMethod());
			authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
			body.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
			byte[] reply = "[{\"district\":\"MUMBAI\"}]".getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", "application/json");
			exchange.sendResponseHeaders(200, reply.length);
			exchange.getResponseBody().write(reply);
			exchange.close();
		});
		server.start();
	}

	@AfterEach
	void stop() {
		server.stop(0);
	}

	@Test
	void sendsBasicAuthAndOnlyFromDateAndToDate() throws Exception {
		RegistryApiAuthConfigEntity config = new RegistryApiAuthConfigEntity();
		config.setServiceName("MCGM");
		config.setAuthType("BASIC");
		config.setUsername("PIRFCMPD2");
		config.setPassword("secret");
		config.setApiData("http://127.0.0.1:" + server.getAddress().getPort() + "/RESTAdapter/Api/SharingShop");
		config.setApiDataMethod("POST");
		config.setDateFromField("from_date");
		config.setDateToField("to_date");
		config.setDateFormat("yyyyMMdd");

		ObjectMapper mapper = new ObjectMapper();
		RegistryApiClient client = new RegistryApiClient(new RestTemplateBuilder(), mapper);

		assertNull(client.login(config));   // no login call for BASIC
		List<JsonNode> records = client.fetchWindow(config, null, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 26));

		assertEquals("POST", method.get());
		assertEquals("Basic " + Base64.getEncoder().encodeToString("PIRFCMPD2:secret".getBytes(StandardCharsets.UTF_8)),
				authorization.get());
		assertEquals(mapper.readTree("{\"from_date\":\"20260101\",\"to_date\":\"20260626\"}"),
				mapper.readTree(body.get()));
		assertEquals(1, records.size());
	}
}
