package com.ticketgo.service;

import com.ticketgo.entity.Customer;

public interface CustomerService {
    void save(Customer customer);

    Customer findById(long customerId);
}
