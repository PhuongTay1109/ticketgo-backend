package com.ticketgo.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserLogoutRequest {
    private String accessToken;
    private String refreshToken;
}
