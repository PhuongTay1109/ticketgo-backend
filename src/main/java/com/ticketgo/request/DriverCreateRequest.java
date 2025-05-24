package com.ticketgo.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DriverCreateRequest {
    private String name;
    private String licenseNumber;
    private String phoneNumber;
    private String imageUrl;
    private String placeOfIssue;
    private LocalDate issueDate;
    private LocalDate expiryDate;
}
