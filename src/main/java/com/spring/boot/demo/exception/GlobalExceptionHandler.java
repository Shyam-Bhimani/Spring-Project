package com.spring.boot.demo.exception;

import com.spring.boot.demo.dto.ErrorResponse;
import com.spring.boot.demo.dto.FieldValidationError;
import com.spring.boot.demo.dto.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException ex, HttpServletRequest request){
        ErrorResponse error = new ErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLoanNotFoundException(LoanNotFoundException ex, HttpServletRequest request){
        ErrorResponse error = new ErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(LoanBusinessException.class)
    public ResponseEntity<ErrorResponse> handleLoanBussinessException(LoanBusinessException ex,HttpServletRequest request){
        ErrorResponse error = new ErrorResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "Bussiness Rule Violation",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex,HttpServletRequest request){
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error->error.getField()+" : "+error.getDefaultMessage())
                .findFirst()
                .orElse("Validation Failed");

        List<FieldValidationError> fieldErrors = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(error->new FieldValidationError(error.getField(), error.getDefaultMessage()))
                .toList();

        ValidationErrorResponse error = new ValidationErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {


        ErrorResponse error = new ErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
