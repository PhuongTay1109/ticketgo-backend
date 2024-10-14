package com.ticketgo.service;

import com.ticketgo.dto.request.CustomerRegistrationRequest;
import com.ticketgo.dto.request.UserLoginRequest;
import com.ticketgo.dto.response.UserLoginResponse;

public interface AuthenticationService {
    void registerCustomer(CustomerRegistrationRequest request);
    void activateAccount(String token);

    UserLoginResponse login(UserLoginRequest request);
}
