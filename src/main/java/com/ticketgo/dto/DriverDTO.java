package com.ticketgo.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DriverDTO {
    private Long driverId;
    private String name;
    private String licenseNumber;
    private String phoneNumber;
    private String imageUrl;
    private String placeOfIssue;
    private String issueDate;
    private String expiryDate;
}

