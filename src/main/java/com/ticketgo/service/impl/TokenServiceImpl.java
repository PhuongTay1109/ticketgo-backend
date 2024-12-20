package com.ticketgo.service.impl;

import com.ticketgo.exception.AppException;
import com.ticketgo.model.TokenType;
import com.ticketgo.model.User;
import com.ticketgo.repository.TokenRepository;
import com.ticketgo.service.TokenService;
import com.ticketgo.model.Token;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepo;

    @Override
    public Token createToken(User user, TokenType tokenType) {
        Token token = Token.builder()
                .user(user)
                .value(UUID.randomUUID().toString())
                .tokenType(tokenType)
                .build();

        return tokenRepo.save(token);
    }

    @Override
    public Token findByValue(String token) {
        return tokenRepo.findByValue(token)
                .orElseThrow(() -> new AppException(
                        "Token không hợp lệ",
                        HttpStatus.BAD_REQUEST
                ));
    }

    @Override
    public void deleteToken(Token token) {
        tokenRepo.delete(token);
    }

    @Override
    public boolean isExpired(Token token) {
        return token.getExpiresAt().isBefore(LocalDateTime.now());
    }
}
