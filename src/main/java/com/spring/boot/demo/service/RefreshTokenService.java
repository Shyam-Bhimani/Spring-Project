package com.spring.boot.demo.service;

import com.spring.boot.demo.entity.AppUser;
import com.spring.boot.demo.entity.RefreshToken;
import com.spring.boot.demo.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-expiration-ms}")
    private long refreshExpiration;

    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String createRefreshToken(AppUser user) {

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setTokenHash(hashToken(rawToken));
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setExpiryDate(
                Instant.now().plusMillis(refreshExpiration)
        );
        refreshToken.setRevokedAt(null);

        refreshTokenRepository.save(refreshToken);

        // Return raw token only to the client.
        return rawToken;
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getEncoder()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(
                    "SHA-256 algorithm unavailable", ex
            );
        }
    }

    public RefreshToken verifyRefreshToken(String rawToken) {

        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token not found")
                );

        if (refreshToken.getRevokedAt() != null) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    public void revokeToken(RefreshToken refreshToken) {

        refreshToken.setRevokedAt(Instant.now());

        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public String rotateRefreshToken(String rawToken) {

        RefreshToken oldToken = verifyRefreshToken(rawToken);

        AppUser user = oldToken.getUser();

        revokeToken(oldToken);

        return createRefreshToken(user);
    }

    @Transactional
    public void revokeRefreshToken(String rawToken) {

        RefreshToken refreshToken = verifyRefreshToken(rawToken);

        revokeToken(refreshToken);
    }
}