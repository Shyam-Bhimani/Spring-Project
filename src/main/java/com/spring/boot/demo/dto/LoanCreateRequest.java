package com.spring.boot.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record LoanCreateRequest(
        @NotNull
        Long customerId,
        @NotNull
        @Positive
        BigDecimal amount,
        @NotNull
        @Positive
        Double interestRate,
        @NotNull
        @Positive
        Integer tenureMonths) {
}
