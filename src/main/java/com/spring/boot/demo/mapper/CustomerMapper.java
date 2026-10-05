package com.spring.boot.demo.mapper;

import com.spring.boot.demo.dto.CustomerCreateRequest;
import com.spring.boot.demo.dto.CustomerResponse;
import com.spring.boot.demo.entity.Customer;

public class CustomerMapper {
    public static CustomerResponse toResponse(Customer customer){
        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone()
        );
    }

    public static Customer toEntity(CustomerCreateRequest request){
        Customer customer = new Customer();

        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());

        return customer;
    }
}
