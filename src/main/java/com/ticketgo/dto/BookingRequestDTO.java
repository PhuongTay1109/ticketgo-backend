package com.ticketgo.dto;

import com.ticketgo.enums.PaymentStatus;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequestDTO {
    private Long scheduleId;
    private Long pickupStopId;
    private Long dropoffStopId;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private List<String> ticketCodes;
    private Double price;
    private PaymentStatus paymentStatus;
}
