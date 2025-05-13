package com.ticketgo.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingInfoDTO {
    private Long bookingId;
    private String bookingDate;
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

