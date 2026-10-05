package com.spring.boot.demo.service;

import com.spring.boot.demo.dto.CustomerCreateRequest;
import com.spring.boot.demo.dto.CustomerResponse;
import com.spring.boot.demo.entity.Customer;
import com.spring.boot.demo.exception.CustomerNotFoundException;
import com.spring.boot.demo.mapper.CustomerMapper;
import com.spring.boot.demo.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.plaf.PanelUI;

import java.util.List;
import java.util.stream.Collectors;

import static com.spring.boot.demo.mapper.CustomerMapper.toResponse;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerResponse create(CustomerCreateRequest request){
        Customer customer = new Customer();
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());

        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    public CustomerResponse getById(Long customerId){
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(()-> new CustomerNotFoundException(customerId));
        return toResponse(customer);
    }

    public List<CustomerResponse> getAll(){
        return customerRepository.findAll().stream().map(CustomerMapper::toResponse).collect(Collectors.toList());
    }

    public CustomerResponse update(Long customerId, CustomerCreateRequest request){
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(()->new CustomerNotFoundException(customerId));

        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());

        Customer updated = customerRepository.save(customer);
        return  toResponse(updated);
    }

    public void delete(Long customerId){
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(()-> new CustomerNotFoundException(customerId));
        customerRepository.delete(customer);
    }
}
