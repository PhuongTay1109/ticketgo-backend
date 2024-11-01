package com.ticketgo.service.impl;

import com.ticketgo.model.Customer;
import com.ticketgo.repository.CustomerRepository;
import com.ticketgo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepo;

    @Override
    public void save(Customer customer) {
        customerRepo.save(customer);
    }
}
