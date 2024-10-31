package com.ticketgo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatStatusDTO {
    private String seatNumber;
    @JsonProperty("isBooked")
    private boolean isBooked;
}

