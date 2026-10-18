package com.spring.boot.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="customers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Customer extends Person{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @OneToMany(mappedBy = "customer")
    private List<Loan> loans;

    private String address;
    @Column(
            name = "first_name",
            nullable = false,
            length = 100
    )
    private String firstName;
    private String lastName;
    private String phone;
    @Column(
            nullable = false,
            unique = true,
            length = 150
    )
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
