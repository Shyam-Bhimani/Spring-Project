package com.spring.boot.demo.repository;

import com.spring.boot.demo.dto.CustomerLoanTotal;
import com.spring.boot.demo.dto.LoanStatusCount;
import com.spring.boot.demo.entity.Customer;
import com.spring.boot.demo.entity.Loan;
import com.spring.boot.demo.entity.LoanStatus;
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

    // Make sure it takes 'LoanStatus status', not 'String status'

    @Query("""
            SELECT l
            FROM Loan l
            JOIN l.customer c
            WHERE c.email = :email"""
            )
    List<Loan> findLoansByCustomerEmail(@Param("email") String email);

    @Query(
            """
                    select l FROM Loan l JOIN l.customer c WHERE c.firstName= :firstName
                    """
    )
    List<Loan> findLoansByCustomerFirstName(@Param("firstName") String firstName);

    @Query("SELECT l FROM Loan l JOIN FETCH l.customer")
    List<Loan> findAllLoansWithCustomer();

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = :status")
    long countLoansByStatus(@Param("status") String status);

    long countByStatus(LoanStatus loanStatus);

    @Query("SELECT l.status AS status, COUNT(l) AS count FROM Loan l GROUP BY l.status")
    List<LoanStatusCount> countLoansByStatus();

    @Query("Select SUM(l.amount) from Loan l where l.status = :status")
    long sumOfApprovedLoans(@Param("status") LoanStatus status);

    @Query("""
           SELECT c.customerId AS customerId,
                  c.email AS email,
                  COALESCE(SUM(l.amount), 0) AS totalAmount
           FROM Loan l
           JOIN l.customer c
           GROUP BY c.customerId, c.email
           """)
    List<CustomerLoanTotal> findTotalLoanAmountPerCustomer();

    @Query("""
           SELECT c
           FROM Loan l
           JOIN l.customer c
           GROUP BY c
           HAVING SUM(l.amount) > :minAmount
           """)
    List<Customer> findCustomersWithTotalLoanGreaterThan(@Param("minAmount") BigDecimal minAmount);

    Page<Loan> findByStatus(LoanStatus status, Pageable pageable);

}
