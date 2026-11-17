package com.spring.boot.demo.exception;

public class DuplicateCustomerEmailException extends RuntimeException {
    public DuplicateCustomerEmailException(String message) {
        super(message);
    }
}
