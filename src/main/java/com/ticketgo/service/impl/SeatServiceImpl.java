package com.ticketgo.service.impl;

import com.ticketgo.dto.SeatStatusDTO;
import com.ticketgo.model.Booking;
import com.ticketgo.model.BookingSeat;
import com.ticketgo.model.Schedule;
import com.ticketgo.model.Seat;
import com.ticketgo.repository.BookingRepository;
import com.ticketgo.repository.BookingSeatRepository;
import com.ticketgo.repository.ScheduleRepository;
import com.ticketgo.repository.SeatRepository;
import com.ticketgo.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {
    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final BookingSeatRepository bookingSeatRepository;

    @Override
    public Integer getBookedSeatsCountForSchedule(Long scheduleId) {
        return bookingRepository.countBookedSeatsForSchedule(scheduleId);
    }

    @Override
    public List<SeatStatusDTO> getSeatStatusForSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        List<Seat> seats = seatRepository.findByBus(schedule.getBus());
        List<Booking> bookings = bookingRepository.findBySchedule(schedule);

        Set<String> bookedSeatNumbers = new HashSet<>();

        for (Booking booking : bookings) {
            BookingSeat bookingSeat = bookingSeatRepository.findByBooking(booking);
            if (bookingSeat != null) {
                bookedSeatNumbers.add(bookingSeat.getSeat().getSeatNumber());
            }
        }

        return seats.stream()
                .map(seat -> new SeatStatusDTO(seat.getSeatNumber(), bookedSeatNumbers.contains(seat.getSeatNumber())))
                .collect(Collectors.toList());
    }
}
