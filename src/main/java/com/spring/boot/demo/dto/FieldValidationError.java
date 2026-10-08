package com.spring.boot.demo.dto;

public record FieldValidationError(
        String field,
        String message
) {
}
