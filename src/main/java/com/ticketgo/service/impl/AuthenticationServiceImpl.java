package com.ticketgo.service.impl;

import com.ticketgo.dto.request.CustomerRegistrationRequest;
import com.ticketgo.dto.request.UserLoginRequest;
import com.ticketgo.dto.response.FacebookUserInfoResponse;
import com.ticketgo.dto.response.GoogleUserInfoResponse;
import com.ticketgo.dto.response.UserLoginResponse;
import com.ticketgo.exception.AppException;
import com.ticketgo.mapper.CustomerMapper;
import com.ticketgo.model.*;
import com.ticketgo.service.*;
import com.ticketgo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final TokenService tokenService;
    private final UserService userService;
    private final CustomerService customerService;
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public void registerCustomer(CustomerRegistrationRequest request) {
        String email = request.getEmail();

        if (userService.existsByEmail(email)) {
            throw new AppException(
                    "Tài khoản với tên email này đã tồn tại",
                    HttpStatus.CONFLICT
            );
        }

        Customer customer = customerMapper.toCustomer(request, passwordEncoder);
        customerService.save(customer);

        Token token = tokenService.createToken(customer, TokenType.ACTIVATION);
        emailService.sendActivationEmail(email, token.getValue())
                .thenAccept(success -> {
                    if (success) {
                        log.info("Email sent successfully!");
                    } else {
                        log.error("Email sending failed.");
                    }
                })
                .exceptionally(ex -> {
                    log.error("Failed to send email: {}", ex.getMessage());
                    return null;
                });

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

        User user = userService.findByEmail(email);

        if (!user.isEnabled()) {
            throw new AppException(
                    "Vui lòng kiểm tra email của bạn để xác minh tài khoản",
                    HttpStatus.UNAUTHORIZED
            );
        }

        if (user.getIsLocked()) {
            throw new AppException(
                    "Tài khoản của bạn đã bị vô hiệu hóa bởi nhà xe",
                    HttpStatus.UNAUTHORIZED
            );
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AppException(
                    "Mật khẩu không đúng",
                    HttpStatus.UNAUTHORIZED
            );
        }

        return getUserLoginResponse(user);
    }

    @Override
    public UserLoginResponse googleLogin(String accessToken) {
        final String googleUserInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<GoogleUserInfoResponse> response = restTemplate.exchange(
                googleUserInfoEndpoint, HttpMethod.GET, httpEntity, GoogleUserInfoResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new AppException("Token is not valid", HttpStatus.UNAUTHORIZED);
        }

        GoogleUserInfoResponse userResponse = response.getBody();
        String email = userResponse.getEmail();

        if (userService.existsByEmail(email)) {
            User user = userService.findByEmail(email);
            return getUserLoginResponse(user);
        } else {
            Customer customer = Customer.builder()
                    .email(email)
                    .password("")
                    .fullName(userResponse.getName())
                    .imageUrl(userResponse.getPicture())
                    .isEnabled(userResponse.isEmailVerified())
                    .role(Role.CUSTOMER)
                    .provider(Provider.GOOGLE)
                    .isLocked(false)
                    .build();
            customerService.save(customer);
            return getUserLoginResponse(customer);
        }
    }

    @Override
    public UserLoginResponse facebookLogin(String accessToken) {
        final String facebookUserInfoEndpoint = "https://graph.facebook.com/me?fields=id,first_name,last_name,email,picture";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<FacebookUserInfoResponse> response = restTemplate.exchange(
                facebookUserInfoEndpoint, HttpMethod.GET, httpEntity, FacebookUserInfoResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new AppException("Token is not valid", HttpStatus.UNAUTHORIZED);
        }

        FacebookUserInfoResponse userResponse = response.getBody();
        String email = userResponse.getEmail();

        if (userService.existsByEmail(email)) {
            User user = userService.findByEmail(email);
            return getUserLoginResponse(user);
        } else {
            Customer customer = Customer.builder()
                    .email(userResponse.getEmail())
                    .password("")
                    .fullName(userResponse.getFirstName() + " " + userResponse.getLastName())
                    .imageUrl(userResponse.getPictureUrl())
                    .isEnabled(true)
                    .role(Role.CUSTOMER)
                    .provider(Provider.GOOGLE)
                    .isLocked(false)
                    .build();
            customerService.save(customer);
            return getUserLoginResponse(customer);
        }
    }

    private UserLoginResponse getUserLoginResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new UserLoginResponse(accessToken, refreshToken);
    }
}
