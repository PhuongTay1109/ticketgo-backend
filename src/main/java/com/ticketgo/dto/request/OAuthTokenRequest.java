package com.ticketgo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OAuthTokenRequest {
    @NotBlank(message =  "Token không được để trống")
    @NotNull(message =  "Token không được để trống")
    private String token;
}
