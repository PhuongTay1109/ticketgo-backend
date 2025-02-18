package com.ticketgo.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisKeys {
    public static String tokenKey(String tokenType) {
        return "token:" + tokenType;
    }
}
