package com.ticketgo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@ToString
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BusCompany extends User {
    @Column(nullable = false)
    private String busCompanyName;

    @Column(nullable = false)
    private String contactEmail;

    @Column(nullable = false)
    private String contactPhone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String description;

    @Column(nullable = false)
    private String bannerUrl;

    @Column(nullable = false)
    private String businessRegistrationAddress;

    @Column(nullable = false)
    private String businessLicenseNumber;

    @Column(nullable = false)
    private String licenseIssuer;

    @Column(nullable = false)
    private LocalDate licenseIssueDate;
}
