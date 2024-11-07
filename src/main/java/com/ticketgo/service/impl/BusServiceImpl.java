package com.ticketgo.service.impl;

import com.ticketgo.exception.AppException;
import com.ticketgo.model.Bus;
import com.ticketgo.repository.BusRepository;
import com.ticketgo.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusServiceImpl implements BusService {
    private final BusRepository busRepo;

    @Override
    public Bus findBySchedule(long scheduleId) {
        return busRepo.findBySchedule(scheduleId)
                .orElseThrow(() -> new AppException(
                                "No bus found for schedule: " + scheduleId,
                                HttpStatus.NOT_FOUND));
    }
}
