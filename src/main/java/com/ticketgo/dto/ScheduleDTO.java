package com.ticketgo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {
    private String routeName;
    private String busImage;
    private String busType;
    private LocalDateTime departureTime;
    private String departureLocation;
    private LocalDateTime arrivalTime;
    private String arrivalLocation;
    private double price;
    private int availableSeats;
    private String travelDuration;
}
