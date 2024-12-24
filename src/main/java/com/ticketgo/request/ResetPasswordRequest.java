package com.ticketgo.request;

import lombok.Getter;

@Getter
public class ResetPasswordRequest {
    private String password;
    private String token;
}
