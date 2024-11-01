package com.ticketgo.service;

import com.ticketgo.model.Token;
import com.ticketgo.model.TokenType;
import com.ticketgo.model.User;

public interface TokenService {
    Token createToken(User user, TokenType tokenType);

    Token findByValue(String token);

    void deleteToken(Token activationToken);
}
