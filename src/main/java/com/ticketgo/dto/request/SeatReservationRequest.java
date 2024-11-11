package com.ticketgo.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
public class SeatReservationRequest {
    private List<Long> seatIds;
    private Long scheduleId;

}
