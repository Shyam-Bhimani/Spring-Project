package com.spring.boot.demo.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {}
