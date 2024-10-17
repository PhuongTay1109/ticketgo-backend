package com.ticketgo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ticketgo.model.BusCompany;
import com.ticketgo.model.Customer;
import com.ticketgo.model.User;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    // Constructors for different types of users
    public UserDTO(User user) {
        this.email = user.getEmail();
        this.role = user.getRole().toString();
        this.imageUrl = user.getImageUrl();

        // If the user is a Customer
        if (user instanceof Customer customer) {
            this.fullName = customer.getFullName();
            this.phoneNumber = customer.getPhoneNumber();
            this.dateOfBirth = customer.getDateOfBirth().toString();
        }

        // If the user is a BusCompany
        if (user instanceof BusCompany busCompany) {
            this.busCompanyName = busCompany.getBusCompanyName();
            this.contactEmail = busCompany.getContactEmail();
            this.contactPhone = busCompany.getContactPhone();
            this.address = busCompany.getAddress();
            this.description = busCompany.getDescription();
        }
    }
}

