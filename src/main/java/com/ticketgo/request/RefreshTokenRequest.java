package com.ticketgo.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RefreshTokenRequest {
    private String refreshToken;
}
