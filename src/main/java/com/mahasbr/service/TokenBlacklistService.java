package com.mahasbr.service;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

	 // Store token -> expiry timestamp
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    /**
     * Blacklist a token until its expiry.
     *
     * @param token  JWT token
     * @param expiry Expiration instant of the token
     */
    public void blacklistToken(String token, Instant expiry) {
        blacklist.put(token, expiry);
    }

    /**
     * Check if a token is blacklisted.
     *
     * @param token JWT token
     * @return true if blacklisted and not expired
     */
    public boolean isTokenBlacklisted(String token) {
        Instant expiry = blacklist.get(token);

        if (expiry == null) {
            return false;
        }

        // Remove expired token from blacklist
        if (Instant.now().isAfter(expiry)) {
            blacklist.remove(token);
            return false;
        }

        return true;
    }
}
