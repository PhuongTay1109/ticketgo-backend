package com.ticketgo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CustomerRegistrationRequest {
    @NotBlank(message =  "Email không được để trống")
    private String email;

    @NotBlank(message =  "Mật khẩu không được để trống")
    private String password;

    @NotBlank(message =  "Tên không được để trống")
    private String fullName;

    @NotBlank(message =  "Số điện thoại  không được để trống")
    private String phoneNumber;

    @NotNull(message =  "Ngày sinh không được để trống")
    private LocalDate dateOfBirth;
}
