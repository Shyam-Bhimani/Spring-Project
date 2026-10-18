package com.spring.boot.demo.repository;

import com.spring.boot.demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    @Query("""
            SELECT c
            FROM Customer c
            where c.firstName=:firstName
            """)
    List<Customer> findByFirstName(@Param("firstName")String firstName);
}
