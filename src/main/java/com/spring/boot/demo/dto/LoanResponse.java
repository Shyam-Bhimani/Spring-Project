package com.spring.boot.demo.dto;

import com.spring.boot.demo.entity.LoanStatus;

import java.math.BigDecimal;

public record LoanResponse(
        Long id,
        Long customerId,
        BigDecimal amount,
        Double interestRate,
        Integer tenureMonths,
        LoanStatus status
) {
}
