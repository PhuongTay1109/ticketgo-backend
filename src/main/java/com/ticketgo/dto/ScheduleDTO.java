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
    private long scheduleId;
    private String routeName;
    private String busImage;
    private String busType;
    private String busId;
    private LocalDateTime departureTime;
    private String departureLocation;
    private String departureAddress;
    private String arrivalAddress;
    private LocalDateTime arrivalTime;
    private String arrivalLocation;
    private double price;
    private int availableSeats;
    private String travelDuration;
}
