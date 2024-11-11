package com.ticketgo.service;

import com.ticketgo.dto.SeatDTO;
import com.ticketgo.dto.request.SeatReservationRequest;
import com.ticketgo.dto.request.TotalPriceCalculationRequest;
import com.ticketgo.dto.response.TotalPriceCalculationResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface SeatService {
    Map<String, List<List<SeatDTO>>> getSeatStatusForSchedule(Long scheduleId);

    int countAvailableSeatsBySchedule(Long scheduleId);

    void reserveSeats(SeatReservationRequest request);

    void releaseReservedSeatsByCustomer();

    TotalPriceCalculationResponse getSeatPrice(TotalPriceCalculationRequest request);
}
