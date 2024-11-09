package com.ticketgo.service;

import com.ticketgo.dto.SeatStatusDTO;
import com.ticketgo.dto.request.SeatReservationRequest;

import java.util.List;

public interface SeatService {
    List<SeatStatusDTO> getSeatStatusForSchedule(Long scheduleId);

    int countAvailableSeatsBySchedule(Long scheduleId);

    void reserveSeats(SeatReservationRequest request);
}
