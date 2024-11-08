package com.ticketgo.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
public class SeatReservationRequest {
    private List<SeatSchedulePair> seatSchedulePairs;

    @Getter
    @Setter
    public static class SeatSchedulePair {
        private Long seatId;
        private Long scheduleId;
    }
}
