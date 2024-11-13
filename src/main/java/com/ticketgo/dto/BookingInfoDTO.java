package com.ticketgo.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingInfoDTO {
    private String ticketCode;
    private String contactName;
    private String routeName;
    private String departureDate;
    private String pickupTime;
    private String pickupLocation;
    private String dropoffLocation;
    private String seatNumber;
    private String licensePlate;
    private String contactEmail;
    private String price;
}

