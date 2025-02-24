package com.ticketgo.service.impl;

import com.ticketgo.constant.RedisKeys;
import com.ticketgo.entity.User;
import com.ticketgo.enums.TokenType;
import com.ticketgo.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RedissonClient redisson;
    private static final long TOKEN_EXPIRATION_TIME = 1;

    @Override
    public String createToken(User user, TokenType tokenType) {
        String redisKey = RedisKeys.tokenKey(tokenType.name());
        String token = UUID.randomUUID().toString();

        RMapCache<String, String> map = redisson.getMapCache(redisKey);
        map.put(token, user.getUserId().toString(), TOKEN_EXPIRATION_TIME, TimeUnit.DAYS);

        return token;
    }

    @Override
    public void deleteToken(String token, TokenType tokenType) {
        String redisKey = RedisKeys.tokenKey(tokenType.name());
        RMapCache<String, String> map = redisson.getMapCache(redisKey);
        map.remove(token);
    }

    @Override
    public boolean isExpired(String token, TokenType tokenType) {
        String redisKey = RedisKeys.tokenKey(tokenType.name());
        return redisson.getMapCache(redisKey).get(token) == null;
    }

    @Override
    public long getUserId(String token, TokenType tokenType) {

        String redisKey = RedisKeys.tokenKey(tokenType.name());
        RMapCache<String, String> map = redisson.getMapCache(redisKey);

        return Long.parseLong(map.get(token));
    }
}
