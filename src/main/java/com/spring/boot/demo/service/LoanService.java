package com.spring.boot.demo.service;

import com.spring.boot.demo.dto.CustomerLoanTotal;
import com.spring.boot.demo.dto.LoanCreateRequest;
import com.spring.boot.demo.dto.LoanResponse;
import com.spring.boot.demo.dto.LoanStatusCount;
import com.spring.boot.demo.entity.AppUser;
import com.spring.boot.demo.entity.Customer;
import com.spring.boot.demo.entity.Loan;
import com.spring.boot.demo.entity.LoanStatus;
import com.spring.boot.demo.exception.CustomerNotFoundException;
import com.spring.boot.demo.exception.LoanBusinessException;
import com.spring.boot.demo.exception.LoanNotFoundException;
import com.spring.boot.demo.mapper.LoanMapper;
import com.spring.boot.demo.repository.AppUserRepository;
import com.spring.boot.demo.repository.CustomerRepository;
import com.spring.boot.demo.repository.LoanRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.spring.boot.demo.mapper.LoanMapper.toResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final AppUserRepository appUserRepository;

    @Transactional
    public LoanResponse create(LoanCreateRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.customerId()));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AppUser loggedInUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found: " + email));

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setUser(loggedInUser); // Set BEFORE save()
        loan.setAmount(request.amount());
        loan.setInterestRate(request.interestRate());
        loan.setTenureMonths(request.tenureMonths());
        loan.setStatus(LoanStatus.PENDING);

        Loan saved = loanRepository.save(loan);
        log.info("Loan Saved!");

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public LoanResponse getById(Long loanId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // 2. Retrieve the requested loan
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        // 3. Business rule: ADMIN can view any loan; CUSTOMER can only view their own
        if (!isAdmin) {
            String loanOwnerEmail = loan.getUser().getEmail();
            if (!loanOwnerEmail.equalsIgnoreCase(currentUserEmail)) {
                // 4. Reject access to another customer's loan (returns HTTP 403 Forbidden)
                throw new AccessDeniedException("You are not authorized to access this loan.");
            }
        }
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

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(timeout = 5)
    public LoanResponse approve(Long loanId){
        Loan loan = loanRepository.findById(loanId).orElseThrow(()->new LoanNotFoundException(loanId));

        if(loan.getStatus()!=LoanStatus.PENDING){
            throw new LoanBusinessException("Only PENDING loans can be APPROVED!");
        }
        loan.setStatus(LoanStatus.APPROVED);
        log.info("Loan Approved!");
        //throw new LoanBusinessException("Error to check rollback");
        return toResponse(loanRepository.save(loan));
    }

    @PreAuthorize("hasRole('ADMIN')")
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

    public List<Loan> getLoansByCustomerEmail(String email) {
        return loanRepository.findLoansByCustomerEmail(email);
    }

    public List<Loan> getLoansByCustomerEmailNative(String email) {
        return loanRepository.findLoansByCustomerEmailNative(email);
    }

    public List<Loan> getLoansByCustomerFirstName(String email) {
        return loanRepository.findLoansByCustomerFirstName(email);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAllLoansWithCustomer();
    }

    public Map<String, Long> getLoanCountsByStatus() {
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("APPROVED", loanRepository.countByStatus(LoanStatus.APPROVED));
        counts.put("PENDING", loanRepository.countByStatus(LoanStatus.PENDING));
        counts.put("REJECTED", loanRepository.countByStatus(LoanStatus.REJECTED));
        return counts;
    }

    public long getSumOfApprovedLoans(){
        return loanRepository.sumOfApprovedLoans(LoanStatus.APPROVED);
    }

    public Map<LoanStatus, Long> getGroupedLoanCounts() {
        Map<LoanStatus, Long> result = new LinkedHashMap<>();

        // Optional: Pre-fill with 0L so statuses with 0 loans still appear
        for (LoanStatus status : LoanStatus.values()) {
            result.put(status, 0L);
        }

        for (LoanStatusCount row : loanRepository.countLoansByStatus()) {
            result.put(row.getStatus(), row.getCount());
        }

        return result;
    }

    public List<CustomerLoanTotal> getTotalLoanAmountPerCustomer() {
        return loanRepository.findTotalLoanAmountPerCustomer();
    }

    public List<Customer> getCustomersWithTotalLoanGreaterThan(BigDecimal minAmount) {
        return loanRepository.findCustomersWithTotalLoanGreaterThan(minAmount);
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
    public Page<Loan> getLoans(String statusStr, int pageNumber, int pageSize,String sortBy,
                               String direction) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page Number cannot be negative");
        }
        if (pageSize < 1 || pageNumber > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        // 1. Convert the String to your Enum type safely
        LoanStatus status;
        try {
            status = LoanStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid loan status: " + statusStr);
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // 2. Pass the Enum type to your repository method
        return loanRepository.findByStatus(status, pageable);
    }
}
