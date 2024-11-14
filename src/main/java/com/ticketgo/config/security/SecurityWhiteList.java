package com.ticketgo.config.security;

public class SecurityWhiteList {
    private static final String[] WHITELIST ={
            "/api/v1/auth/**",
            "/api/v1/routes/**",
            "/api/v1/schedules/**",
            "/api/v1/route-stops/**",
            "/api/v1/seats",
            "/api/v1/policies/**",
            "/api/v1/amenities/**",
            "/api/v1/payment/vnpay/return",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    public static String[] getWhiteList() {
        return WHITELIST;
    }
}
