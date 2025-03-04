package com.ticketgo.request;

import lombok.Getter;

@Getter
public class DriverCreateRequest {
    private String name;
    private String licenseNumber;
    private String phoneNumber;
    private String imageUrl;
}
