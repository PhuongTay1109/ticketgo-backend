package com.ticketgo.request;

import java.util.List;

import lombok.Getter;

@Getter
public class SeatReservationRequest {
    private List<String> ticketCodes;
}
