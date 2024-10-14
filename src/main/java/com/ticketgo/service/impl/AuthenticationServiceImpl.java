package com.ticketgo.service.impl;

import com.ticketgo.dto.request.CustomerRegistrationRequest;
import com.ticketgo.dto.request.UserLoginRequest;
import com.ticketgo.dto.response.UserLoginResponse;
import com.ticketgo.exception.AppException;
import com.ticketgo.model.*;
import com.ticketgo.service.*;
import com.ticketgo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final TokenService tokenService;
    private final UserService userService;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void registerCustomer(CustomerRegistrationRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String fullName = request.getFullName();
        String phoneNumber = request.getPhoneNumber();
        LocalDate dateOfBirth = request.getDateOfBirth();

        if (userService.existsByEmail(email)) {
            throw new AppException(
                    "Tài khoản với tên email " + email + " đã tồn tại",
                    HttpStatus.CONFLICT
            );
        }

        Customer customer = Customer.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .imageUrl("https://res.cloudinary.com/dj1h07rea/image/upload/v1728906155/sbcf-default-avatar_iovbch.webp")
                .fullName(fullName)
                .phoneNumber(phoneNumber)
                .dateOfBirth(dateOfBirth)
                .isEnabled(false)
                .isLocked(false)
                .role(Role.CUSTOMER)
                .build();

        customerService.save(customer);

        Token token = tokenService.createToken(customer, TokenType.ACTIVATION);

        emailService.sendActivationEmail(email, token.getToken());
    }

    @Override
    @Transactional
    public void activateAccount(String token) {
        Token activationToken = tokenService.findByToken(token);

        if(activationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(
                    "Đường link đã hết hạn. Vui lòng chọn gửi lại đường link mới!",
                    HttpStatus.GONE
            );
        }

        Customer customer = (Customer) activationToken.getUser();

        if (customer.isEnabled()) {
            throw new AppException("Tài khoản này đã được kích hoạt", HttpStatus.CONFLICT);
        }

        customer.setIsEnabled(true);
        customerService.save(customer);

        tokenService.deleteToken(activationToken);
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        User user = (User) userService.loadUserByUsername(email);

        if (!user.isEnabled()) {
            throw new AppException("Vui lòng kiểm tra email của bạn để xác minh tài khoản", HttpStatus.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AppException("Tên người dùng hoặc mật khẩu không hợp lệ", HttpStatus.UNAUTHORIZED);
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new UserLoginResponse(accessToken, refreshToken);
    }
}
