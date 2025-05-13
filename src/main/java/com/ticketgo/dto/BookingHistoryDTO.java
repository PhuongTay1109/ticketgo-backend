package com.ticketgo.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingHistoryDTO {
    private Long bookingId;
    private String ticketCode;
    private String contactName;
    private String routeName;
    private String departureDate;
    private String pickupTime;
    private String pickupLocation;
    private String dropoffLocation;
    private List<String> seatNumbers;
    private String licensePlate;
    private String contactEmail;
    private String originalPrice;
    private String discountedPrice;
    private String status;
    private String refundAmount;
    private String refundStatus;
    private String refundReason;
    private String refundDate;
}
