package com.ticketgo.service.impl;

import com.ticketgo.dto.SeatStatusDTO;
import com.ticketgo.dto.request.SeatReservationRequest;
import com.ticketgo.model.Customer;
import com.ticketgo.repository.SeatRepository;
import com.ticketgo.service.SeatService;

import com.ticketgo.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepo;
    private final TicketService ticketService;

    @Override
    public List<SeatStatusDTO> getSeatStatusForSchedule(Long scheduleId) {
        return seatRepo.findSeatStatusesByScheduleId(scheduleId);
    }

    @Override
    public int countAvailableSeatsByScheduleId(Long scheduleId) {
        return seatRepo.countAvailableSeatsByScheduleId(scheduleId);
    }

    @Override
    public void reserveSeats(SeatReservationRequest request) {
        List<SeatReservationRequest.SeatSchedulePair> seatSchedulePairs =
                request.getSeatSchedulePairs();

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        Customer customer = (Customer) authentication.getPrincipal();
        long customerId = customer.getUserId();

        for(SeatReservationRequest.SeatSchedulePair pair : seatSchedulePairs) {
            long seatId = pair.getSeatId();
            long scheduleId = pair.getScheduleId();
            ticketService.reserveSeats(scheduleId, seatId, customerId);
        }
    }
}
