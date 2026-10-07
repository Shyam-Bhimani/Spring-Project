package com.spring.boot.demo.mapper;

import com.spring.boot.demo.dto.LoanResponse;
import com.spring.boot.demo.entity.Loan;
import com.spring.boot.demo.entity.LoanStatus;

import java.math.BigDecimal;


public class LoanMapper {
    public static LoanResponse toResponse(Loan loan){
        return new LoanResponse(
                loan.getLoanId(),
                loan.getCustomer().getCustomerId(),
                loan.getAmount(),
                loan.getInterestRate(),
                loan.getTenureMonths(),
                loan.getStatus()
        );
    }
}
