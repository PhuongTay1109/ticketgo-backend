package com.ticketgo.service.impl;

import com.ticketgo.dto.SeatPriceDTO;
import com.ticketgo.model.SeatType;
import com.ticketgo.repository.SeatPricingRepository;
import com.ticketgo.service.SeatPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatPricingServiceImpl implements SeatPricingService {

    private final SeatPricingRepository seatPricingRepo;

    @Override
    public double findPriceByScheduleIdAndSeatType(long scheduleId, SeatType seatType) {
        return seatPricingRepo.findPriceByScheduleIdAndSeatType(scheduleId, seatType);
    }

    @Override
    public List<SeatPriceDTO> getSeatPricesByScheduleId(long scheduleId) {
        return seatPricingRepo.getSeatPricesByScheduleId(scheduleId);
    }
}
