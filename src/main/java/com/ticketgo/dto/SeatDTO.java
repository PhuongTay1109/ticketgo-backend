package com.ticketgo.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatDTO {
    private String ticketCode;
    private String seatNumber;
    private Boolean isAvailable;
}
