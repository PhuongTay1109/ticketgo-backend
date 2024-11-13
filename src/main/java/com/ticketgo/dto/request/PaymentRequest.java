package com.ticketgo.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentRequest {
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private Long pickupStopId;
    private Long dropoffStopId;
    private Long totalPrice;
    private Long customerId;
}