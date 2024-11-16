package com.ticketgo.dto.request;

import lombok.Getter;

@Getter
public class ResetPasswordRequest {
    private String password;
    private String token;
}
