package com.ticketgo.request;

import lombok.Data;

import java.util.List;

@Data
public class SaveBookingInfoRequest {
    private List<String> ticketCodes;
    private Long pickupStopId;
    private Long dropoffStopId;
    private Long scheduleId;
}
