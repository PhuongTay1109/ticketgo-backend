package com.ticketgo.request;

import lombok.Getter;

@Getter
public class UserLogoutRequest {
    private String accessToken;
    private String refreshToken;
}
