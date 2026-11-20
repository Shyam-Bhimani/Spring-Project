package com.spring.boot.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email is required field")
        @Email(message = "Invalid Email format")
        String email,
        @NotBlank(message = "Password is required field")
        @Size(min = 8,max = 72,message = "Password must be between 8 to 72 characters")
        String password
) {}
