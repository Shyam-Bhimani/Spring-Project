package com.spring.boot.demo.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long customerId){
        super("Customer Not found with : "+customerId);
    }
}
