package com.mahasbr.service;

import java.time.Duration;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundles;
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

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RegistryApiClient.class);

	private static final List<String> TOKEN_FIELDS = List.of("token", "Token", "access_token", "accessToken", "jwt");
	private static final List<String> LIST_FIELDS = List.of("Response", "response", "data", "Data", "result",
			"Result", "records", "Records");

	private final RestTemplate restTemplate;
	private RestTemplate insecureRestTemplate;   // created on first use, see templateFor()
	private final ObjectMapper objectMapper;

	/** Without a custom certificate bundle (used by tests). */
	public RegistryApiClient(RestTemplateBuilder builder, ObjectMapper objectMapper) {
		this(builder, objectMapper, null, null);
	}

	/**
	 * @param sslBundleName optional name of a Spring SSL bundle (property
	 *                      {@code app.registry-sync.ssl-bundle}) whose truststore holds the
	 *                      certificates of department servers that use a private or self-signed
	 *                      certificate. Certificate checking stays ON; the bundle only adds trust.
	 */
	@Autowired
	public RegistryApiClient(RestTemplateBuilder builder, ObjectMapper objectMapper,
			ObjectProvider<SslBundles> sslBundles, @Value("${app.registry-sync.ssl-bundle:}") String sslBundleName) {
		// Timeouts: a department server that hangs must not block the sync forever.
		RestTemplateBuilder configured = builder
				.setConnectTimeout(Duration.ofSeconds(15))
				.setReadTimeout(Duration.ofMinutes(3));
		if (sslBundleName != null && !sslBundleName.isBlank() && sslBundles != null) {
			configured = configured.setSslBundle(sslBundles.getObject().getBundle(sslBundleName.trim()));
		}
		this.restTemplate = configured.build();
		this.objectMapper = objectMapper;
	}

	/** BASIC APIs send the username/password with every request and have no login call. */
	public static boolean isBasic(RegistryApiAuthConfigEntity config) {
		return "BASIC".equalsIgnoreCase(config.getAuthType());
	}

	/** Returns the bearer token, or null for BASIC APIs (nothing to log in to). */
	public String login(RegistryApiAuthConfigEntity config) {
		if (isBasic(config)) {
			return null;
		}
		Map<String, String> credentials = Map.of(
				"Username", config.getUsername(),
				"Password", config.getPassword() == null ? "" : config.getPassword());

		ResponseEntity<String> response = send(templateFor(config), config.getAuthUrl(), config.getAuthUrlMethod(),
				credentials, null);
		String body = response.getBody() == null ? "" : response.getBody().trim();

		String token = body.startsWith("{") ? tokenFromJson(body) : body.replace("\"", "");
		if (token == null || token.isBlank()) {
			throw new IllegalStateException("Login to " + config.getServiceName() + " returned no token.");
		}
		return token;
	}

	/**
	 * The request that fetchWindow sends, as readable text (the password is never included).
	 * Shown on the settings page when a call fails, so a wrong setting is visible at once.
	 */
	public String describeRequest(RegistryApiAuthConfigEntity config, LocalDate from, LocalDate to) {
		return config.getApiDataMethod() + " " + config.getApiData() + " | login: "
				+ (isBasic(config) ? "BASIC as " + config.getUsername() : "TOKEN") + " | body: "
				+ dateRange(config, from, to);
	}

	private Map<String, String> dateRange(RegistryApiAuthConfigEntity config, LocalDate from, LocalDate to) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(
				config.getDateFormat() == null || config.getDateFormat().isBlank() ? "yyyy-MM-dd" : config.getDateFormat());
		String fromField = config.getDateFromField() == null || config.getDateFromField().isBlank()
				? "FromDate" : config.getDateFromField();
		String toField = config.getDateToField() == null || config.getDateToField().isBlank()
				? "ToDate" : config.getDateToField();
		for (String field : List.of(fromField, toField)) {
			if (!field.matches("^[A-Za-z_][A-Za-z0-9_]*$")) {
				// Settings saved before this was validated, e.g. a date value typed as the field name.
				throw new IllegalStateException("Wrong setting for " + config.getServiceName() + ": the date field name is '"
						+ field + "', which is not a field name. Open Edit on the Registry API Settings page, set the "
						+ "names (e.g. from_date / to_date) and the date format, and Save.");
			}
		}
		Map<String, String> range = new java.util.LinkedHashMap<>();
		range.put(fromField, from.format(format));
		range.put(toField, to.format(format));
		return range;
	}

	/** Returns the raw JSON of each establishment in the date range (inclusive). */
	public List<JsonNode> fetchWindow(RegistryApiAuthConfigEntity config, String token, LocalDate from,
			LocalDate to) {
		Map<String, String> range = dateRange(config, from, to);
		ResponseEntity<String> response = isBasic(config)
				? sendBasic(templateFor(config), config.getApiData(), config.getApiDataMethod(), range,
						config.getUsername(), config.getPassword())
				: send(templateFor(config), config.getApiData(), config.getApiDataMethod(), range, token);

		String body = response.getBody();
		// Debug aid: the raw response, before any parsing or mapping (first 1500 characters only).
		log.info("{} raw response for {} to {}: HTTP {}, {} characters: {}", config.getServiceName(), from, to,
				response.getStatusCode().value(), body == null ? 0 : body.length(),
				body == null ? "(empty)" : abbreviate(body, 1500));
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

	/**
	 * The normal client (certificates checked), or, only for an API whose "skip certificate check"
	 * option an admin ticked, a client that accepts any certificate. Never used for other APIs.
	 */
	private RestTemplate templateFor(RegistryApiAuthConfigEntity config) {
		return Boolean.TRUE.equals(config.getSkipSslVerify()) ? insecureTemplate() : restTemplate;
	}

	private synchronized RestTemplate insecureTemplate() {
		if (insecureRestTemplate == null) {
			try {
				SSLContext trustAll = SSLContext.getInstance("TLS");
				trustAll.init(null, new TrustManager[] { new X509TrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType) {
					}

					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType) {
					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return new X509Certificate[0];
					}
				} }, new SecureRandom());

				SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory() {
					@Override
					protected void prepareConnection(HttpURLConnection connection, String httpMethod)
							throws IOException {
						super.prepareConnection(connection, httpMethod);
						if (connection instanceof HttpsURLConnection https) {
							https.setSSLSocketFactory(trustAll.getSocketFactory());
							https.setHostnameVerifier((host, session) -> true);
						}
					}
				};
				factory.setConnectTimeout(15_000);
				factory.setReadTimeout(180_000);
				insecureRestTemplate = new RestTemplate(factory);
			} catch (GeneralSecurityException e) {
				throw new IllegalStateException("Could not prepare the HTTPS client", e);
			}
		}
		return insecureRestTemplate;
	}

	private ResponseEntity<String> sendBasic(RestTemplate template, String url, String method,
			Map<String, String> values, String username, String password) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(username == null ? "" : username, password == null ? "" : password,
				StandardCharsets.UTF_8);
		return exchange(template, url, method, values, headers);
	}

	private ResponseEntity<String> send(RestTemplate template, String url, String method,
			Map<String, String> values, String bearerToken) {
		HttpHeaders headers = new HttpHeaders();
		if (bearerToken != null) {
			headers.setBearerAuth(bearerToken);
		}
		return exchange(template, url, method, values, headers);
	}

	private ResponseEntity<String> exchange(RestTemplate restTemplate, String url, String method,
			Map<String, String> values, HttpHeaders headers) {
		headers.setAccept(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));

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

	/** The list of records inside a wrapper object such as {"Response":[...]} or {"data":[...]}. */
	private static JsonNode findList(JsonNode root) {
		for (String field : LIST_FIELDS) {
			JsonNode candidate = root.get(field);
			if (candidate != null && candidate.isArray()) {
				return candidate;
			}
		}
		// Any other wrapper name: use the object's only list, if there is exactly one.
		JsonNode onlyList = null;
		for (JsonNode value : root) {
			if (value.isArray()) {
				if (onlyList != null) {
					return null;   // more than one list: ambiguous
				}
				onlyList = value;
			}
		}
		return onlyList;
	}

	private static String abbreviate(String text) {
		return abbreviate(text, 200);
	}

	private static String abbreviate(String text, int max) {
		String oneLine = text.replaceAll("\\s+", " ");
		return oneLine.length() <= max ? oneLine : oneLine.substring(0, max) + "...";
	}
}
