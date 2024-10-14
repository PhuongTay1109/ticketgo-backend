package com.ticketgo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserLoginRequest {
    @NotBlank(message =  "Email không được để trống")
    @NotNull(message =  "Email không được để trống")
    private String email;

    @NotBlank(message =  "Mật khẩu không được để trống")
    @NotNull(message =  "Mật khẩu không được để trống")
    private String password;
}
