package com.ticketgo.service;

import com.ticketgo.dto.SeatDTO;
import com.ticketgo.request.SeatReservationRequest;
import com.ticketgo.request.PriceEstimationRequest;
import com.ticketgo.response.PriceEstimationResponse;

import java.util.List;
import java.util.Map;

public interface SeatService {
    Map<String, List<List<SeatDTO>>> getSeatStatusForSchedule(Long scheduleId);

    int countAvailableSeatsBySchedule(Long scheduleId);

    void reserveSeats(SeatReservationRequest request);

    void cancelReservedSeatsByCustomer(long scheduleId, Long returnScheduleId);

    PriceEstimationResponse getSeatPrice(PriceEstimationRequest request);
}
