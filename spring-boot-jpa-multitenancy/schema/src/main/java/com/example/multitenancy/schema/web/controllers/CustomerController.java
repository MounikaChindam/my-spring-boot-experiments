package com.example.multitenancy.schema.web.controllers;

import com.example.multitenancy.schema.domain.request.CustomerDto;
import com.example.multitenancy.schema.entities.Customer;
import com.example.multitenancy.schema.services.CustomerService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getAllCustomers(@RequestParam String tenant) {
        log.info("fetching all customer for tenant : {}", tenant);
        return customerService.findAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(
            @PathVariable Long id, @RequestParam String tenant) {
        log.info("fetching customer by id {} for tenant : {}", id, tenant);
        return customerService
                .findCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer createCustomer(
            @RequestBody @Validated CustomerDto customer, @RequestParam String tenant) {
        log.info("creating customer by for tenant : {}", tenant);
        return customerService.saveCustomer(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerDto customerDto,
            @RequestParam String tenant) {
        log.info("updating customer for id {} in tenant : {}", id, tenant);
        return customerService.updateCustomer(id, customerDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Customer> deleteCustomer(
            @PathVariable Long id, @RequestParam String tenant) {
        log.info("deleting customer by id {} for tenant : {}", id, tenant);
        return customerService
                .findCustomerById(id)
                .map(
                        customer -> {
                            customerService.deleteCustomerById(id);
                            return ResponseEntity.ok(customer);
                        })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
