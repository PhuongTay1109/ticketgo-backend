package com.ticketgo.service;

import com.ticketgo.request.*;
import com.ticketgo.response.RefreshTokenResponse;
import com.ticketgo.response.UserLoginResponse;
import com.ticketgo.entity.Customer;

public interface AuthenticationService {
    void registerCustomer(CustomerRegistrationRequest request);
    void activateAccount(String token);

    UserLoginResponse login(UserLoginRequest request);

    UserLoginResponse googleLogin(String accessToken);

    UserLoginResponse facebookLogin(String accessToken);

    RefreshTokenResponse refreshToken(String refreshToken);

    Customer getAuthorizedCustomer();

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void logout(UserLogoutRequest request);
}
