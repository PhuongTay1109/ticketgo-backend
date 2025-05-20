package com.ticketgo.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingHistoryDTO {
    private Long bookingId;
    private String bookingDate;
    private String seatInfos;
    private String contactName;
    private String routeName;
    private String departureDate;
    private String pickupTime;
    private String pickupLocation;
    private String dropoffLocation;
    private String licensePlate;
    private String contactEmail;
    private String originalPrice;
    private String discountedPrice;
    private String status;
    private String refundAmount;
    private String refundStatus;
    private String refundReason;
    private String refundDate;
    private String driverName;
    private String driverPhone;
}
