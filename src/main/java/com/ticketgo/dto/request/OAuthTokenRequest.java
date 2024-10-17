package com.ticketgo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OAuthTokenRequest {
    @NotBlank(message =  "Token không được để trống")
    private String token;
}
