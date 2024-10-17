package com.ticketgo.mapper;

import com.ticketgo.dto.request.CustomerRegistrationRequest;
import com.ticketgo.model.Customer;
import com.ticketgo.model.Provider;
import com.ticketgo.model.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public Customer toCustomer(CustomerRegistrationRequest request, PasswordEncoder passwordEncoder) {
        return Customer.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .imageUrl("https://res.cloudinary.com/dj1h07rea/image/upload/v1728906155/sbcf-default-avatar_iovbch.webp")
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .isEnabled(false)
                .isLocked(false)
                .role(Role.CUSTOMER)
                .provider(Provider.LOCAL)
                .build();
    }
}
