package com.ticketgo.service;

import com.ticketgo.entity.User;
import com.ticketgo.enums.TokenType;

public interface TokenService {
    String createToken(User user, TokenType tokenType);

    void deleteToken(String token, TokenType tokenType);

    boolean isExpired(String token, TokenType tokenType);

    long getUserId(String token, TokenType tokenType);
}
