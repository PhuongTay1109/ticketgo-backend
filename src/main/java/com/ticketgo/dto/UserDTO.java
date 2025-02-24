package com.ticketgo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private String email;
    private String role;
    private String imageUrl;

    // Customer specific fields
    private String fullName;
    private String phoneNumber;
    private String dateOfBirth;

    // BusCompany specific fields
    private String busCompanyName;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String description;
}

