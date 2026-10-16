package com.spring.boot.demo.service;

import com.spring.boot.demo.dto.LoanCreateRequest;
import com.spring.boot.demo.dto.LoanResponse;
import com.spring.boot.demo.entity.Customer;
import com.spring.boot.demo.entity.Loan;
import com.spring.boot.demo.entity.LoanStatus;
import com.spring.boot.demo.exception.CustomerNotFoundException;
import com.spring.boot.demo.exception.LoanBusinessException;
import com.spring.boot.demo.exception.LoanNotFoundException;
import com.spring.boot.demo.mapper.LoanMapper;
import com.spring.boot.demo.repository.CustomerRepository;
import com.spring.boot.demo.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.spring.boot.demo.mapper.LoanMapper.toResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;

    public LoanResponse create(LoanCreateRequest request) {
        Customer customer =customerRepository.findById(request.customerId())
                .orElseThrow(()-> new CustomerNotFoundException(request.customerId()));
        Loan loan =new Loan();
        loan.setCustomer(customer);
        loan.setAmount(request.amount());
        loan.setInterestRate(request.interestRate());
        loan.setTenureMonths(request.tenureMonths());

        loan.setStatus(LoanStatus.PENDING);
        Loan saved = loanRepository.save(loan);
        log.info("Loan Saved!");

        return toResponse(saved);
    }

    public LoanResponse getById(Long loanId){
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(()-> new LoanNotFoundException(loanId));
        return toResponse(loan);
    }

    public List<LoanResponse> getAll(){
        return loanRepository.findAll().stream().map(LoanMapper::toResponse).toList();
    }

    public LoanResponse update(Long loanId,LoanCreateRequest request){
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(()->new LoanNotFoundException(loanId));

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(()->new CustomerNotFoundException(request.customerId()));

        loan.setCustomer(customer);
        loan.setAmount(request.amount());
        loan.setInterestRate(request.interestRate());
        loan.setTenureMonths(request.tenureMonths());

        return toResponse(loanRepository.save(loan));
    }

    public void delete(Long loanId){
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(()-> new LoanNotFoundException(loanId));

        loanRepository.delete(loan);
    }

    @Transactional(timeout = 5)
    public LoanResponse approve(Long loanId){
        Loan loan = loanRepository.findById(loanId).orElseThrow(()->new LoanNotFoundException(loanId));

        if(loan.getStatus()!=LoanStatus.PENDING){
            throw new LoanBusinessException("Only PENDING loans can be APPROVED!");
        }


        loan.setStatus(LoanStatus.APPROVED);
        log.info("Loan Approved!");
        return toResponse(loanRepository.save(loan));
    }

    @Transactional(timeout = 5)
    public LoanResponse reject(Long loanId){
        Loan loan = loanRepository.findById(loanId).orElseThrow(()->new LoanNotFoundException(loanId));

        if(loan.getStatus()!=LoanStatus.PENDING){
            throw new LoanBusinessException("Only PENDING loans can be REJECTED!");
        }

        loan.setStatus(LoanStatus.REJECTED);
        log.info("Loan Rejected!");
        return  toResponse(loanRepository.save(loan));
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getByStatus(String status) {
        List<Loan> loans = loanRepository.findByStatus(status);

        return loans.stream().map(LoanMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Loan> getByCustomerLoans(Long customerId){
        return loanRepository.findByCustomer_CustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Loan> getHighValueLoans(BigDecimal minimumAmount){
        return loanRepository.findByAmountGreaterThan(minimumAmount);
    }

    @Transactional(readOnly = true)
    public Page<Loan> getLoans(String statusStr, int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page Number cannot be negative");
        }
        if (pageSize < 1 || pageNumber > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        // 1. Convert the String to your Enum type safely
        LoanStatus status;
        try {
            status = LoanStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid loan status: " + statusStr);
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "loanId"));

        // 2. Pass the Enum type to your repository method
        return loanRepository.findByStatus(status, pageable);
    }
}
