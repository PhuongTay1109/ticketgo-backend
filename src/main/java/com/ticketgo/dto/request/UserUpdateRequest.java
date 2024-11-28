package com.ticketgo.dto.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserUpdateRequest {
    private String fullName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
}
