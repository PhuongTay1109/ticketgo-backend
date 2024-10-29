package com.ticketgo.service;

import com.ticketgo.dto.request.CustomerRegistrationRequest;
import com.ticketgo.dto.request.UserLoginRequest;
import com.ticketgo.dto.response.RefreshTokenResponse;
import com.ticketgo.dto.response.UserLoginResponse;

public interface AuthenticationService {
    void registerCustomer(CustomerRegistrationRequest request);
    void activateAccount(String token);

    UserLoginResponse login(UserLoginRequest request);

    UserLoginResponse googleLogin(String accessToken);

    UserLoginResponse facebookLogin(String accessToken);

    RefreshTokenResponse refreshToken(String refreshToken);
}
