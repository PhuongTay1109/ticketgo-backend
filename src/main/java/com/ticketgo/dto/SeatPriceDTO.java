package com.ticketgo.dto;

import com.ticketgo.model.SeatType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatPriceDTO {
    private SeatType seatType;
    private double seatPrice;
}

