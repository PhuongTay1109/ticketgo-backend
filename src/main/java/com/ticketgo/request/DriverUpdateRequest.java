package com.ticketgo.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DriverUpdateRequest {
    private String name;
    private String licenseNumber;
    private String phoneNumber;
    private String imageUrl;
}
