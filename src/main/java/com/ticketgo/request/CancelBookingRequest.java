package com.ticketgo.request;

import lombok.Data;

@Data
public class CancelBookingRequest {
    private long bookingId;
    private Double amount;
    private String reason;
}
