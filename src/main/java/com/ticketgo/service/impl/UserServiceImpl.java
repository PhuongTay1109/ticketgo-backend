package com.ticketgo.service.impl;

import com.ticketgo.dto.CustomerContactInfoDTO;
import com.ticketgo.dto.UserDTO;
import com.ticketgo.request.UserUpdateRequest;
import com.ticketgo.exception.AppException;
import com.ticketgo.mapper.UserMapper;
import com.ticketgo.entity.Customer;
import com.ticketgo.entity.User;
import com.ticketgo.repository.CustomerRepository;
import com.ticketgo.repository.UserRepository;
import com.ticketgo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new AppException(
                        "Tài khoản với tên emai này không tồn tại",
                        HttpStatus.NOT_FOUND
                ));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new AppException(
                        "Tài khoản với tên email này không tồn tại",
                        HttpStatus.NOT_FOUND
                ));
    }

    @Override
    public UserDTO getUserDetails() {
        User user = getAuthorizedUser();
        return UserMapper.INSTANCE.mapUser(user);
    }

    @Override
    public CustomerContactInfoDTO getCustomerContactIno() {
        Customer customer = getAuthorizedCustomer();
        return CustomerContactInfoDTO.builder()
                .fullName(customer.getFullName())
                .phoneNumber(customer.getPhoneNumber())
                .email(customer.getEmail())
                .build();
    }

    @Override
    public void updateUser(UserUpdateRequest request) {
        Customer customer = getAuthorizedCustomer();
        long id = customer.getUserId();
        int rowUpdated = customerRepo.updateCustomerFields(
                id,
                request.getFullName(),
                request.getPhoneNumber(),
                request.getDateOfBirth()
        );

        if (rowUpdated == 0) {
            throw new AppException("Customer not found with id: " + id, HttpStatus.NOT_FOUND);
        }
    }

    private Customer getAuthorizedCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Customer) authentication.getPrincipal();
    }

    private User getAuthorizedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
