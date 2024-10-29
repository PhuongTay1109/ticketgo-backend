package com.ticketgo.config.security;

public class SecurityWhiteList {
    private static final String[] WHITELIST ={
            "/api/v1/auth/**",
            "/api/v1/routes/**",
            "/api/v1/schedules/**",
            "/api/v1/route-stops/**"
    };

    public static String[] getWhiteList() {
        return WHITELIST;
    }
}
