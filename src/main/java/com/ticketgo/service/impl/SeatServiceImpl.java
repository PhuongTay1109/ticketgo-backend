package com.ticketgo.service.impl;

import com.ticketgo.dto.SeatStatusDTO;
import com.ticketgo.repository.SeatRepository;
import com.ticketgo.service.SeatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepo;

    @Override
    public Integer getBookedSeatsCountForSchedule(Long scheduleId) {
        return seatRepo.countBookedSeatsForSchedule(scheduleId);
    }

    @Override
    public List<SeatStatusDTO> getSeatStatusForSchedule(Long scheduleId) {
        return seatRepo.getSeatStatusForSchedule(scheduleId);
    }
}
