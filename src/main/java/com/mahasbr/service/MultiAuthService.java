package com.mahasbr.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * MultiAuthService is responsible for handling authentication with multiple external APIs.
 *
 * ✅ Responsibilities:
 * - Retrieves and caches tokens per API configuration.
 * - Refreshes tokens when they are missing or about to expire.
 * - Parses JWT tokens to extract expiry information.
 *
 * ⚠️ Industry Standards / Recommendations:
 * - Thread-safe token caching using ConcurrentHashMap.
 * - Synchronized access for token refresh to avoid race conditions.
 * - Grace period (10 seconds before expiry) to ensure proactive token renewal.
 * - Uses SLF4J logging instead of System.out.println for maintainability.
 */
@Service
public class MultiAuthService {

    private static final Logger log = LoggerFactory.getLogger(MultiAuthService.class);

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    // Cache for tokens and their expiration times
    private final Map<ApiAuthConfig, String> tokenCache = new ConcurrentHashMap<>();
    private final Map<ApiAuthConfig, Instant> expiryCache = new ConcurrentHashMap<>();

    public MultiAuthService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Retrieves a valid token for the given API configuration.
     *
     * - Checks the cache first.
     * - If the token is missing or about to expire, refreshes it.
     * - Adds a small buffer (10 seconds) before expiry to avoid last-second failures.
     *
     * @param config API authentication configuration (username, password, login URL).
     * @return A valid JWT token as String.
     */
    public synchronized String getToken(ApiAuthConfig config) {
        String token = tokenCache.get(config);
        Instant expiry = expiryCache.get(config);

        if (token == null || expiry == null || Instant.now().isAfter(expiry.minusSeconds(10))) {
            log.info(" Refreshing token for API: {}", config.name());
            token = authenticate(config);
            tokenCache.put(config, token);
        } else {
            log.debug("Using cached token for API: {}", config.name());
        }
        return token;
    }

    /**
     * Authenticates against the external API to retrieve a new token.
     *
     * - Sends credentials (username/password) to login URL.
     * - Parses the JWT token and extracts the "exp" (expiry) claim.
     * - Caches the expiry time for proactive renewal.
     * - Falls back to default expiry (3000s) if parsing fails.
     *
     * @param config API authentication configuration.
     * @return A fresh JWT token.
     */
    private String authenticate(ApiAuthConfig config) {
        Map<String, String> body = Map.of(
                "Username", config.getUsername(),
                "Password", config.getPassword()
        );

        // Execute login call
        String token = webClient.post()
                .uri(config.getLoginUrl())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (token == null) {
            log.error("❌ Authentication failed for API: {}", config.name());
            throw new RuntimeException("Authentication failed for API: " + config.name());
        }

        token = token.replace("\"", ""); // Clean JSON string response

        try {
            // Parse JWT and extract expiry claim
            String[] parts = token.split("\\.");
            if (parts.length > 1) {
                String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
                Map<String, Object> claims = mapper.readValue(payloadJson, Map.class);
                Number exp = (Number) claims.get("exp");

                expiryCache.put(
                        config,
                        exp != null ? Instant.ofEpochSecond(exp.longValue()) : Instant.now().plusSeconds(3000)
                );
            } else {
                expiryCache.put(config, Instant.now().plusSeconds(3000));
            }
        } catch (Exception e) {
            log.warn("⚠️ Failed to parse token expiry for API: {}. Using fallback expiry.", config.name(), e);
            expiryCache.put(config, Instant.now().plusSeconds(3000));
        }

        log.info("🔑 Token refreshed for API: {}", config.name());
        return token;
    }
}