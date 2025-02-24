package com.ticketgo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserLoginRequest {
    @NotBlank(message =  "Email không được để trống")
    private String email;

    @NotBlank(message =  "Mật khẩu không được để trống")
    private String password;
}
