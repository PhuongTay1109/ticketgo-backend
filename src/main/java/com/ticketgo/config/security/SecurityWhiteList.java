package com.ticketgo.config.security;

public class SecurityWhiteList {
    private static final String[] WHITELIST ={
            "/api/v1/auth/**",
            "/api/v1/routes/**",
            "/api/v1/schedules/**"
    };

    public static String[] getWhiteList() {
        return WHITELIST;
    }
}
