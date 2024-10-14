package com.ticketgo.service;

import com.ticketgo.model.Token;
import com.ticketgo.model.TokenType;
import com.ticketgo.model.User;

import java.util.Optional;

public interface TokenService {
    Token createToken(User user, TokenType tokenType);

    Token findByToken(String token);

    void deleteToken(Token activationToken);
}
