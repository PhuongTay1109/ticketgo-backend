package com.ticketgo.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BusUpdateRequest {
    private String busImage;
    private String registrationExpiry;
    private String expirationDate;
}
