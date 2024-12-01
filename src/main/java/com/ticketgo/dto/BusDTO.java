package com.ticketgo.dto;

import lombok.Data;

@Data
public class BusDTO {
    private Long busId;
    private String licensePlate;
    private String busType;
    private String busImage;
    private Integer totalSeats;
    private Integer floors;
    private String registrationExpiry;
    private String expirationDate;
}

