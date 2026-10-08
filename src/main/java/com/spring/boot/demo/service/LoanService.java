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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
