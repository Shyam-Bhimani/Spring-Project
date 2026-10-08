package com.spring.boot.demo.dto;

import java.time.Instant;
import java.util.List;

public record ValidationErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldValidationError> errors
) {
}
