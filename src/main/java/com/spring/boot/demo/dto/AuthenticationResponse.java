package com.spring.boot.demo.dto;

public record AuthenticationResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}
