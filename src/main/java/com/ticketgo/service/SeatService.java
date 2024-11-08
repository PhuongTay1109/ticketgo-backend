package com.ticketgo.service;

import com.ticketgo.dto.SeatStatusDTO;
import com.ticketgo.dto.request.SeatReservationRequest;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatService {
    List<SeatStatusDTO> getSeatStatusForSchedule(Long scheduleId);

    int countAvailableSeatsByScheduleId(@Param("scheduleId") Long scheduleId);

    void reserveSeats(SeatReservationRequest request);
}
