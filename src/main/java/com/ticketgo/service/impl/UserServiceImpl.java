package com.ticketgo.service.impl;

import com.ticketgo.dto.UserDTO;
import com.ticketgo.exception.AppException;
import com.ticketgo.mapper.UserMapper;
import com.ticketgo.model.User;
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
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return UserMapper.INSTANCE.mapUser(user);
    }
}
