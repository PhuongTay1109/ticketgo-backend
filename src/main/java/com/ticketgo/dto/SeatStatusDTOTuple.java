package com.ticketgo.dto;

import com.ticketgo.model.SeatType;

public interface SeatStatusDTOTuple {
    Long getSeatId();
    String getSeatNumber();
    Integer getIsBooked();
    SeatType getSeatType();
}

