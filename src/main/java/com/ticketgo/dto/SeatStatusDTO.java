package com.ticketgo.dto;

import com.ticketgo.model.SeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatStatusDTO {
    private String seatNumber;
    private Boolean isBooked;
    private SeatType seatType;
}

