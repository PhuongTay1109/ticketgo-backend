package com.ticketgo.service;

import com.ticketgo.dto.SeatDTO;
import com.ticketgo.dto.request.SeatReservationRequest;
import com.ticketgo.dto.request.PriceEstimationRequest;
import com.ticketgo.dto.response.PriceEstimationResponse;

import java.util.List;
import java.util.Map;

public interface SeatService {
    Map<String, List<List<SeatDTO>>> getSeatStatusForSchedule(Long scheduleId);

    int countAvailableSeatsBySchedule(Long scheduleId);

    void reserveSeats(SeatReservationRequest request);

    void cancelReservedSeatsByCustomer();

    PriceEstimationResponse getSeatPrice(PriceEstimationRequest request);
}
