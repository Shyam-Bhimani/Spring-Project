package com.spring.boot.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;
    private Long customerId;
    private BigDecimal amount;
    private Double interestRate;
    private Integer tenureMonths;
    private LoanStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
