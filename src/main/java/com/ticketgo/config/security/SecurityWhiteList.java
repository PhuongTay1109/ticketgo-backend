package com.ticketgo.config.security;

public class SecurityWhiteList {
    private static final String[] WHITELIST ={
            "/api/v1/auth/**"
    };

    public static String[] getWhiteList() {
        return WHITELIST;
    }
}
