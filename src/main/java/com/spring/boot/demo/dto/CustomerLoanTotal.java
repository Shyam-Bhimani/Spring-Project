package com.spring.boot.demo.dto;

import java.math.BigDecimal;

public interface CustomerLoanTotal {
    Long getCustomerId();
    String getEmail();
    BigDecimal getTotalAmount(); // Change to Double if Loan.amount is Double
}
