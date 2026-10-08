package com.spring.boot.demo.controller;

import com.spring.boot.demo.dto.LoanCreateRequest;
import com.spring.boot.demo.dto.LoanResponse;
import com.spring.boot.demo.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponse> create(@Valid @RequestBody LoanCreateRequest request){
        LoanResponse response = loanService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getById(@PathVariable("id") Long loanId){ // Added explicit path binding
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
    public ResponseEntity<LoanResponse> update(@PathVariable("id") Long loanId, @Valid @RequestBody LoanCreateRequest request){ // Added explicit path binding
        LoanResponse response = loanService.update(loanId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response); // Updated to return the modified object body
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long loanId){ // Added explicit path binding
        loanService.delete(loanId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LoanResponse> approve(@PathVariable("id") Long loanId){ // Added explicit path binding
        LoanResponse response = loanService.approve(loanId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<LoanResponse> reject(@PathVariable("id") Long loanId){ // Added explicit path binding
        LoanResponse response = loanService.reject(loanId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
