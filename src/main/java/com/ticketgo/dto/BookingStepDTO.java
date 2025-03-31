package com.ticketgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingStepDTO {
    private int step;
    private String vnPayUrl;

    public BookingStepDTO(int step) {
        this.step = step;
    }
}
