package com.spring.boot.demo.exception;

public class LoanBusinessException
        extends RuntimeException {

    public LoanBusinessException(String message) {
        super(message);
    }
}
