package com.ticketgo.service;

import com.ticketgo.dto.SeatStatusDTO;

import java.util.List;

public interface SeatService {
    Integer getBookedSeatsCountForSchedule(Long scheduleId);
    List<SeatStatusDTO> getSeatStatusForSchedule(Long scheduleId);
}
