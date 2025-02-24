package com.ticketgo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AccountActivationRequest {
    @NotBlank(message =  "Token không được để trống")
    private String token;
}

