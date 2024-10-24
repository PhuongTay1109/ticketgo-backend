package com.ticketgo.service.impl;

import com.ticketgo.repository.BookingRepository;
import com.ticketgo.service.BookingService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    @Override
    public Integer getBookedSeatsCountForSchedule(Long scheduleId) {
        return bookingRepository.countBookedSeatsForSchedule(scheduleId);
    }
}
