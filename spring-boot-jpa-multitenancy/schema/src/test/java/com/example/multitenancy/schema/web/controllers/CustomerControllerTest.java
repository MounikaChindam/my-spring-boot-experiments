package com.example.multitenancy.schema.web.controllers;

import static com.example.multitenancy.schema.utils.AppConstants.PROFILE_TEST;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.multitenancy.schema.config.multitenancy.TenantIdentifierResolver;
import com.example.multitenancy.schema.domain.request.CustomerDto;
import com.example.multitenancy.schema.entities.Customer;
import com.example.multitenancy.schema.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CustomerController.class)
@ActiveProfiles(PROFILE_TEST)
class CustomerControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CustomerService customerService;
    @MockBean private TenantIdentifierResolver tenantIdentifierResolver;

    private List<Customer> customerList;
    private final String tenant = "test1";

    @BeforeEach
    void setUp() {
        this.customerList = new ArrayList<>();
        this.customerList.add(new Customer(1L, "text 1"));
        this.customerList.add(new Customer(2L, "text 2"));
        this.customerList.add(new Customer(3L, "text 3"));
    }

    @Test
    void shouldFetchAllCustomers() throws Exception {
        given(customerService.findAllCustomers()).willReturn(this.customerList);

        this.mockMvc
                .perform(get("/api/customers").param("tenant", tenant))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(customerList.size())));
    }

    @Test
    void shouldFindCustomerById() throws Exception {
        Long customerId = 1L;
        Customer customer = new Customer(customerId, "text 1");
        given(customerService.findCustomerById(customerId)).willReturn(Optional.of(customer));

        this.mockMvc
                .perform(get("/api/customers/{id}", customerId).param("tenant", tenant))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(customer.getName())));
    }

    @Test
    void shouldReturn404WhenFetchingNonExistingCustomer() throws Exception {
        Long customerId = 1L;
        given(customerService.findCustomerById(customerId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(get("/api/customers/{id}", customerId).param("tenant", tenant))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewCustomer() throws Exception {
        Customer customer = new Customer(1L, "some text");
        CustomerDto customerDto = new CustomerDto("some text");
        given(customerService.saveCustomer(any(CustomerDto.class))).willReturn(customer);
        this.mockMvc
                .perform(
                        post("/api/customers")
                                .param("tenant", tenant)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(customerDto.name())));
    }

    @Test
    void shouldReturn400WhenCreateNewCustomerWithoutText() throws Exception {
        Customer customer = new Customer(null, null);

        this.mockMvc
                .perform(
                        post("/api/customers")
                                .param("tenant", tenant)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("about:blank")))
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andExpect(jsonPath("$.instance", is("/api/customers")))
                .andReturn();
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        Long customerId = 1L;
        Customer customer = new Customer(customerId, "Updated text");
        CustomerDto customerDto = new CustomerDto("Updated text");
        given(customerService.updateCustomer(eq(customerId), any(CustomerDto.class)))
                .willReturn(ResponseEntity.ok(customer));

        this.mockMvc
                .perform(
                        put("/api/customers/{id}", customer.getId())
                                .param("tenant", tenant)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(customerDto.name())));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingCustomer() throws Exception {
        Long customerId = 1L;
        given(customerService.updateCustomer(eq(customerId), any(CustomerDto.class)))
                .willReturn(ResponseEntity.notFound().build());
        Customer customer = new Customer(customerId, "Updated text");

        this.mockMvc
                .perform(
                        put("/api/customers/{id}", customerId)
                                .param("tenant", tenant)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        Long customerId = 1L;
        Customer customer = new Customer(customerId, "Some text");
        given(customerService.findCustomerById(customerId)).willReturn(Optional.of(customer));
        doNothing().when(customerService).deleteCustomerById(customer.getId());

        this.mockMvc
                .perform(delete("/api/customers/{id}", customer.getId()).param("tenant", tenant))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(customer.getName())));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingCustomer() throws Exception {
        Long customerId = 1L;
        given(customerService.findCustomerById(customerId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(delete("/api/customers/{id}", customerId).param("tenant", tenant))
                .andExpect(status().isNotFound());
    }
}
