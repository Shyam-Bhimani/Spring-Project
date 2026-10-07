package com.spring.boot.demo.exception;

public class LoanNotFoundException extends RuntimeException{
    public LoanNotFoundException(Long loanId){
        super("Loan not Found with id : "+loanId);
    }
}
