package com.ticketgo.service.impl;

import com.ticketgo.constant.RedisKeys;
import com.ticketgo.entity.BaseEntity;
import com.ticketgo.entity.Customer;
import com.ticketgo.entity.User;
import com.ticketgo.enums.MembershipLevel;
import com.ticketgo.enums.Provider;
import com.ticketgo.enums.Role;
import com.ticketgo.enums.TokenType;
import com.ticketgo.exception.AppException;
import com.ticketgo.request.*;
import com.ticketgo.response.FacebookUserInfoResponse;
import com.ticketgo.response.GoogleUserInfoResponse;
import com.ticketgo.response.RefreshTokenResponse;
import com.ticketgo.response.UserLoginResponse;
import com.ticketgo.service.*;
import com.ticketgo.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final TokenService tokenService;
    private final UserService userService;
    private final CustomerService customerService;
    private final EmailService emailService;
    private final RedissonClient redisson;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtil;

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

        Customer customer = BaseEntity.CustomerMapper.INSTANCE.toCustomer(request, passwordEncoder);
        customer.setPoints(0);
        customer.setLevel(MembershipLevel.NEW_PASSENGER);
        customerService.save(customer);

        String token = tokenService.createToken(customer, TokenType.ACTIVATION);
        emailService.sendActivationEmail(email, token)
                .thenAccept(success -> {
                    if (success) {
                        log.info("Email sent successfully to: {}", request.getEmail());
                    } else {
                        log.error("Email sending failed to: {}", request.getEmail());
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
        long userId;
        try {
            userId = tokenService.getUserId(token, TokenType.ACTIVATION);
        } catch (Exception e) {
            throw new AppException("Token không hợp lệ", HttpStatus.BAD_REQUEST);
        }

        if (tokenService.isExpired(token, TokenType.ACTIVATION)) {
            Customer customer = customerService.findById(userId);
            String newToken = tokenService.createToken(customer, TokenType.ACTIVATION);
            emailService.sendActivationEmail(customer.getEmail(), newToken);
            tokenService.deleteToken(token, TokenType.ACTIVATION);
            throw new AppException(
                    "Đường link đã hết hạn. Một đường link kích hoạt tài khoản mới đã được gửi đến email của bạn.",
                    HttpStatus.GONE
            );
        }

        Customer customer = customerService.findById(userId);
        if (customer.isEnabled()) {
            throw new AppException("Tài khoản này đã được kích hoạt", HttpStatus.CONFLICT);
        }

        customer.setIsEnabled(true);
        customerService.save(customer);
        tokenService.deleteToken(token, TokenType.ACTIVATION);
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
    @Transactional
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
            throw new RuntimeException("Token is invalid");
        }

        GoogleUserInfoResponse userResponse = response.getBody();

        if (userResponse == null) {
            throw new RuntimeException("There is no information about this user");
        }

        String email = userResponse.getEmail();

        if (userService.existsByEmail(email)) {
            User user = userService.findByEmail(email);
            user.setImageUrl(userResponse.getPicture());

            return getUserLoginResponse(user);
        } else {
            Customer customer = Customer.builder()
                    .email(email)
                    .password("")
                    .fullName(userResponse.getName())
                    .imageUrl(userResponse.getPicture())
                    .isEnabled(userResponse.isEmailVerified())
                    .role(Role.ROLE_CUSTOMER)
                    .provider(Provider.GOOGLE)
                    .isLocked(false)
                    .build();
            customerService.save(customer);

            return getUserLoginResponse(customer);
        }
    }

    @Override
    @Transactional
    public UserLoginResponse facebookLogin(String accessToken) {
        final String facebookUserInfoEndpoint =
                "https://graph.facebook.com/me?fields=id,first_name,last_name,email,picture";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<FacebookUserInfoResponse> response = restTemplate.exchange(
                facebookUserInfoEndpoint, HttpMethod.GET, httpEntity, FacebookUserInfoResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Token is invalid");
        }

        FacebookUserInfoResponse userResponse = response.getBody();

        if (userResponse == null) {
            throw new RuntimeException("There is no information about this user");
        }

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
                    .role(Role.ROLE_CUSTOMER)
                    .provider(Provider.GOOGLE)
                    .isLocked(false)
                    .build();
            customerService.save(customer);

            return getUserLoginResponse(customer);
        }
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {
        try {
            if (jwtUtil.isTokenValid(refreshToken)) {
                String email = jwtUtil.extractUsername(refreshToken);
                String accessToken = jwtUtil.generateAccessToken(userService.findByEmail(email));

                return new RefreshTokenResponse(accessToken);
            }
        } catch (Exception e) {
            log.warn("Invalid refresh token", e);
        }
        throw new AppException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
    }

    @Override
    public Customer getAuthorizedCustomer() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
       return (Customer) authentication.getPrincipal();
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();
        Customer customer = (Customer) userService.findByEmail(email);
        if(customer.getProvider() == Provider.GOOGLE) {
            throw new AppException(
                    "Bạn đã đăng nhập email này với tài khoản của Google. Hãy chọn Đăng nhập với Google để tiếp tục.",
                    HttpStatus.BAD_REQUEST);
        }

        String token = tokenService.createToken(customer, TokenType.RESET_PASSWORD);
        emailService.sendResetPasswordEmail(email, token);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String token = request.getToken();
        long userId;
        try {
            userId = tokenService.getUserId(token, TokenType.RESET_PASSWORD);
        } catch (Exception e) {
            throw new AppException("Token không hợp lệ", HttpStatus.BAD_REQUEST);
        }

        if(tokenService.isExpired(token, TokenType.RESET_PASSWORD)) {
            tokenService.deleteToken(token, TokenType.RESET_PASSWORD);
            throw new AppException(
                    "Đường link đã hết hạn!",
                    HttpStatus.GONE
            );
        }

        Customer customer = customerService.findById(userId);
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customerService.save(customer);
        tokenService.deleteToken(token, TokenType.RESET_PASSWORD);
    }

    @Override
    public void logout(UserLogoutRequest request) {
        String redisKey = RedisKeys.blackListTokenKey;
        RList<String> blackList = redisson.getList(redisKey);
        blackList.add(request.getAccessToken());
        blackList.add(request.getRefreshToken());
    }

    private UserLoginResponse getUserLoginResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new UserLoginResponse(accessToken, refreshToken);
    }
}
