package com.spring.boot.demo.dto;

import com.spring.boot.demo.entity.LoanStatus;

public interface LoanStatusCount {
    LoanStatus getStatus();
    Long getCount();
}
