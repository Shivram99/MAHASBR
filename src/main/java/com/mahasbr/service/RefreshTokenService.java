package com.mahasbr.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mahasbr.entity.RefreshTokenEntity;
import com.mahasbr.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // Refresh token validity = 30 minutes
    private static final long REFRESH_TOKEN_VALIDITY = 30 * 60 * 1000; 

    /**
     * Create a new refresh token for the given username
     */
    public RefreshTokenEntity createRefreshToken(String username) {
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setUsername(username);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_VALIDITY));
        return refreshTokenRepository.save(refreshToken);
    }

   
    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    
    public boolean validateRefreshToken(RefreshTokenEntity tokenEntity) {
        return tokenEntity.getExpiryDate().isAfter(Instant.now());
    }

    /**
     * Revoke all refresh tokens for a user (used on logout)
     */
    @Transactional
    public void revoke(String username) {
        refreshTokenRepository.deleteByUsername(username);
    }
}
