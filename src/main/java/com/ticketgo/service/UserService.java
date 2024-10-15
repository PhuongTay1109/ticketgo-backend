package com.ticketgo.service;

import com.ticketgo.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    boolean existsByEmail(String email);

    User findByEmail(String email);
}
