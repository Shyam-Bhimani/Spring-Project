package com.spring.boot.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Request to create new loan")
public record LoanCreateRequest(
        @Schema(description = "Customer ID",example = "1" )
        @NotNull
        Long customerId,
        @Schema(
                description = "Loan amount",
                example = "500000.00"
        )
        @NotNull
        @Positive
        BigDecimal amount,

        @Schema(
                description = "Annual interest rate",
                example = "8.5"
        )
        @NotNull
        @Positive
        Double interestRate,

        @Schema(
                description = "Loan tenure in months",
                example = "60"
        )
        @NotNull
        @Positive
        Integer tenureMonths) {
}
