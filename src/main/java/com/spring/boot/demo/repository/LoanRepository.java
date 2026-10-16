package com.spring.boot.demo.repository;

import com.spring.boot.demo.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan,Long> {
    // 1. Find loans by status
    List<Loan> findByStatus(String status);

    // 2. Find loans for a customer
    List<Loan> findByCustomer_CustomerId(Long customerId);

    // 3. Find loans above a given amount
    List<Loan> findByAmountGreaterThan(
            BigDecimal amount);

    // 4. Find loans by status and minimum amount
    List<Loan> findByStatusAndAmountGreaterThanEqual(
            String status,
            BigDecimal amount);

    // 5. Custom JPQL query
    @Query("""
            SELECT l FROM Loan l WHERE l.status=:status ORDER BY l.amount DESC
            """)
    List<Loan> findByStatusOrderByAmount(@Param("status") String status);

    Page<Loan> findByStatus(String status, Pageable pageable);
}
