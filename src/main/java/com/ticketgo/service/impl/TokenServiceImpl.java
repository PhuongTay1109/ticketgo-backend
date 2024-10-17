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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    public Token createToken(User user, TokenType tokenType) {
        Token token = Token.builder()
                .user(user)
                .value(UUID.randomUUID().toString())
                .tokenType(tokenType)
                .build();

        return tokenRepository.save(token);
    }

    @Override
    public Token findByValue(String token) {
        return tokenRepository.findByValue(token)
                .orElseThrow(() -> new AppException(
                        "Token không hợp lệ",
                        HttpStatus.BAD_REQUEST
                ));
    }

    @Override
    public void deleteToken(Token activationToken) {
        tokenRepository.delete(activationToken);
    }
}
