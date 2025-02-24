package com.ticketgo.request;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class SeatReservationRequest {
    private List<String> ticketCodes;
}
