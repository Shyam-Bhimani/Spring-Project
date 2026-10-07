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
    public ResponseEntity<LoanResponse> getById(@PathVariable Long loanId){
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
    public ResponseEntity<LoanResponse> update(@PathVariable Long loanId,@Valid @RequestBody LoanCreateRequest request){
        LoanResponse response = loanService.update(loanId,request);
        return  ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long loanId){
        loanService.delete(loanId);
        return ResponseEntity.noContent().build();
    }
}
