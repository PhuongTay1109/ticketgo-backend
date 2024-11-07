package com.ticketgo.service.impl;

import com.ticketgo.model.ReservationSeat;
import com.ticketgo.repository.ReservationSeatRepository;
import com.ticketgo.service.ReservationSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationSeatServiceImpl implements ReservationSeatService {
    private final ReservationSeatRepository reservationSeatRepo;

    @Override
    public List<ReservationSeat> getReservationSeatsByScheduleId(long scheduleId) {
        return reservationSeatRepo.findAllByScheduleId(scheduleId);
    }
}
