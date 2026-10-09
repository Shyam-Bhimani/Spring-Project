package com.spring.boot.demo.controller;

import com.spring.boot.demo.dto.LoanCreateRequest;
import com.spring.boot.demo.dto.LoanResponse;
import com.spring.boot.demo.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name="Loan Management",
        description = "APIs for managing customer loans"
)
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @Operation(
            summary = "Create a new loan",
            description = "Creates a new loan for an existing customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Loan created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found"
            )
    })
    @PostMapping
    public ResponseEntity<LoanResponse> create(@Valid @RequestBody LoanCreateRequest request){
        LoanResponse response = loanService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get loan by ID",
            description = "Retrieves a loan using its unique ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Loan Found successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Loan Not Found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getById(
            @Parameter(
                    description = "Unique loan ID",
                    example = "1001"
            )@PathVariable("id") Long loanId){ // Added explicit path binding
        LoanResponse response = loanService.getById(loanId);
        return ResponseEntity
                .status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAll(){
        List<LoanResponse> responses = loanService.getAll();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanResponse> update(@Parameter(
            description = "Unique loan ID",
            example = "1001"
    )@PathVariable("id") Long loanId, @Valid @RequestBody LoanCreateRequest request){ // Added explicit path binding
        LoanResponse response = loanService.update(loanId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response); // Updated to return the modified object body
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(
            description = "Unique loan ID",
            example = "1001"
    )@PathVariable("id") Long loanId){ // Added explicit path binding
        loanService.delete(loanId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LoanResponse> approve(@Parameter(
            description = "Unique loan ID",
            example = "1001"
    )@PathVariable("id") Long loanId){ // Added explicit path binding
        LoanResponse response = loanService.approve(loanId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<LoanResponse> reject(@Parameter(
            description = "Unique loan ID",
            example = "1001"
    )@PathVariable("id") Long loanId){ // Added explicit path binding
        LoanResponse response = loanService.reject(loanId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
