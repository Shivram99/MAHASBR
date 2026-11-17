package com.mahasbr.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.entity.RegistryApiAuthConfigEntity;

/**
 * Talks to one department's registry API, using the URLs and login stored in
 * registry_api_auth_config (nothing is hard-coded).
 *
 * <p>Protocol, as used by the existing department APIs:
 * <ol>
 * <li>Login: {@code {"Username": ..., "Password": ...}} to the login URL; the response is the
 * token, either as plain text or as JSON with a token field.</li>
 * <li>Data: {@code {"FromDate": "yyyy-MM-dd", "ToDate": "yyyy-MM-dd"}} with
 * {@code Authorization: Bearer <token>}; the response is a JSON array of establishments.</li>
 * </ol>
 * For GET the same values are sent as query parameters.
 */
@Component
public class RegistryApiClient {

	private static final List<String> TOKEN_FIELDS = List.of("token", "Token", "access_token", "accessToken", "jwt");
	private static final List<String> LIST_FIELDS = List.of("data", "Data", "result", "Result", "records", "Records");

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	public RegistryApiClient(RestTemplateBuilder builder, ObjectMapper objectMapper) {
		// Timeouts: a department server that hangs must not block the sync forever.
		this.restTemplate = builder
				.setConnectTimeout(Duration.ofSeconds(15))
				.setReadTimeout(Duration.ofMinutes(3))
				.build();
		this.objectMapper = objectMapper;
	}

	public String login(RegistryApiAuthConfigEntity config) {
		Map<String, String> credentials = Map.of(
				"Username", config.getUsername(),
				"Password", config.getPassword() == null ? "" : config.getPassword());

		ResponseEntity<String> response = send(config.getAuthUrl(), config.getAuthUrlMethod(), credentials, null);
		String body = response.getBody() == null ? "" : response.getBody().trim();

		String token = body.startsWith("{") ? tokenFromJson(body) : body.replace("\"", "");
		if (token == null || token.isBlank()) {
			throw new IllegalStateException("Login to " + config.getServiceName() + " returned no token.");
		}
		return token;
	}

	/** Returns the raw JSON of each establishment in the date range (inclusive). */
	public List<JsonNode> fetchWindow(RegistryApiAuthConfigEntity config, String token, LocalDate from,
			LocalDate to) {
		Map<String, String> range = Map.of("FromDate", from.toString(), "ToDate", to.toString());
		ResponseEntity<String> response = send(config.getApiData(), config.getApiDataMethod(), range, token);

		String body = response.getBody();
		if (body == null || body.isBlank() || body.trim().equals("null")) {
			return List.of();
		}
		try {
			JsonNode root = objectMapper.readTree(body);
			JsonNode list = root.isArray() ? root : findList(root);
			if (list == null) {
				throw new IllegalStateException("Expected a JSON list of records from " + config.getServiceName()
						+ " but got: " + abbreviate(body));
			}
			List<JsonNode> records = new ArrayList<>(list.size());
			list.forEach(records::add);
			return records;
		} catch (IllegalStateException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalStateException("Response from " + config.getServiceName() + " is not valid JSON: "
					+ abbreviate(body), e);
		}
	}

	private ResponseEntity<String> send(String url, String method, Map<String, String> values, String bearerToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));
		if (bearerToken != null) {
			headers.setBearerAuth(bearerToken);
		}

		if ("GET".equalsIgnoreCase(method)) {
			UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(url);
			values.forEach(uri::queryParam);
			return restTemplate.exchange(uri.encode().build().toUri(), HttpMethod.GET, new HttpEntity<>(headers),
					String.class);
		}

		headers.setContentType(MediaType.APPLICATION_JSON);
		return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(values, headers), String.class);
	}

	private String tokenFromJson(String body) {
		try {
			JsonNode root = objectMapper.readTree(body);
			for (String field : TOKEN_FIELDS) {
				if (root.hasNonNull(field)) {
					return root.get(field).asText();
				}
				JsonNode data = root.path("data");
				if (data.hasNonNull(field)) {
					return data.get(field).asText();
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	private static JsonNode findList(JsonNode root) {
		for (String field : LIST_FIELDS) {
			JsonNode candidate = root.get(field);
			if (candidate != null && candidate.isArray()) {
				return candidate;
			}
		}
		return null;
	}

	private static String abbreviate(String text) {
		String oneLine = text.replaceAll("\\s+", " ");
		return oneLine.length() <= 200 ? oneLine : oneLine.substring(0, 200) + "...";
	}
}
