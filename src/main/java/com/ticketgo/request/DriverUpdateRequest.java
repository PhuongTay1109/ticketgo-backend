package com.ticketgo.request;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class DriverUpdateRequest {
    private String name;
    private String licenseNumber;
    private String phoneNumber;
    private String imageUrl;
    private String placeOfIssue;
    private LocalDate issueDate;
    private LocalDate expiryDate;
}
