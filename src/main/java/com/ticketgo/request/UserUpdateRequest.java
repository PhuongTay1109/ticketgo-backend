package com.ticketgo.request;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class UserUpdateRequest {
    private String fullName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String imageUrl;
}
