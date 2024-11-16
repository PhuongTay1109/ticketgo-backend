package com.ticketgo.controller;

import com.ticketgo.dto.request.*;
import com.ticketgo.dto.response.ApiResponse;
import com.ticketgo.dto.response.UserLoginResponse;
import com.ticketgo.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ApiResponse registerCutomer(@RequestBody @Valid CustomerRegistrationRequest request) {
        authenticationService.registerCustomer(request);
        return new ApiResponse(
                HttpStatus.CREATED,
                "Đăng ký tài khoản thành công. Vui lòng kiểm tra email để kích hoạt tài khoản!",
                null
        );
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody @Valid UserLoginRequest request) {
        UserLoginResponse resp = authenticationService.login(request);
        return new ApiResponse(
                HttpStatus.OK,
                "Đăng nhập thành công.",
                resp
        );
    }

    @PostMapping("/forgot-password")
    public ApiResponse forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return new ApiResponse(
                HttpStatus.CREATED,
                "Đã gửi link để đặt lại mật khẩu thành công.",
                null
        );
    }

    @PostMapping("/reset-password")
    public ApiResponse resetPassword(@RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return new ApiResponse(
                HttpStatus.CREATED,
                "Đặt lại mật khẩu mới thành công.",
                null
        );
    }

    @PostMapping("/google-login")
    public ApiResponse googleLogin(@RequestBody @Valid OAuthTokenRequest request) {
        UserLoginResponse resp = authenticationService.googleLogin(request.getToken());
        return new ApiResponse(
                HttpStatus.OK,
                "Đăng nhập thành công.",
                resp
        );
    }

    @PostMapping("/facebook-login")
    public ApiResponse facebookLogin(@RequestBody @Valid OAuthTokenRequest request) {
        UserLoginResponse resp = authenticationService.facebookLogin(request.getToken());
        return new ApiResponse(
                HttpStatus.OK,
                "Đăng nhập thành công.",
                resp
        );
    }

    @PutMapping("/activate")
    public ApiResponse activateAccount(@RequestBody @Valid AccountActivationRequest request) {
        authenticationService.activateAccount(request.getToken());
        return new ApiResponse(
                HttpStatus.OK,
                "Tài khoản đã được kích hoạt thành công. Vui lòng đăng nhập!",
                null
        );
    }

    @PostMapping("/refresh-token")
    public ApiResponse refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return new ApiResponse(
                HttpStatus.OK,
                "Refresh token thành công",
                authenticationService.refreshToken(request.getRefreshToken())
        );
    }
}
