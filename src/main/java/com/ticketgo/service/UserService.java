package com.ticketgo.service;

import com.ticketgo.dto.CustomerContactInfoDTO;
import com.ticketgo.dto.UserDTO;
import com.ticketgo.request.UserUpdateRequest;
import com.ticketgo.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    boolean existsByEmail(String email);

    User findByEmail(String email);

    UserDTO getUserDetails();

    CustomerContactInfoDTO getCustomerContactIno();

    void updateUser(UserUpdateRequest request);
}
