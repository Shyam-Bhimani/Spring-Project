package com.spring.boot.demo.dto;

import com.spring.boot.demo.entity.AppUser;

public record UserResponse(
        Long id,
        String email,
        String role,
        boolean enabled
) {
    // Helper method to map from entity to DTO
    public static UserResponse fromEntity(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled()
        );
    }
}