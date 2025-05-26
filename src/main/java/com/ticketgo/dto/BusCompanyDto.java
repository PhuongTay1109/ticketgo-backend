package com.ticketgo.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusCompanyDto {
    private String busCompanyName;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String description;
    private String bannerUrl;
    private String businessRegistrationAddress;
    private String businessLicenseNumber;
    private String licenseIssuer;
    private LocalDate licenseIssueDate;
}
