package com.ticketgo.service.impl;

import com.ticketgo.common.RedisKeys;
import com.ticketgo.dto.CustomerContactInfoDTO;
import com.ticketgo.dto.UserDTO;
import com.ticketgo.entity.Customer;
import com.ticketgo.entity.User;
import com.ticketgo.exception.AppException;
import com.ticketgo.mapper.UserMapper;
import com.ticketgo.repository.CustomerRepository;
import com.ticketgo.repository.UserRepository;
import com.ticketgo.request.UserUpdateRequest;
import com.ticketgo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;
    private final RedissonClient redisson;

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

    @Override
    @Transactional
    public void changeLockStatus(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new AppException("User không tồn tại", HttpStatus.NOT_FOUND));

        boolean currentLockedStatus = user.getIsLocked();
        String redisKey = RedisKeys.blackListUserKey;
        RList<String> blackList = redisson.getList(redisKey);

        if(currentLockedStatus) {
            blackList.add(user.getUsername());
        } else {
            blackList.remove(user.getUsername());
        }

        user.setIsLocked(!user.getIsLocked());
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
