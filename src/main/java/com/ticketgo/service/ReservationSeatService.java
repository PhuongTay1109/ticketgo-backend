package com.ticketgo.service;

import com.ticketgo.model.ReservationSeat;

import java.util.List;

public interface ReservationSeatService {
    List<ReservationSeat> getReservationSeatsByScheduleId(long scheduleId);
}
