package com.ticketgo.service;

import com.ticketgo.dto.SeatPriceDTO;
import com.ticketgo.model.SeatType;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatPricingService {
    double findPriceByScheduleIdAndSeatType(long scheduleId, SeatType seatType);
    List<SeatPriceDTO> getSeatPricesByScheduleId(long scheduleId);
}
