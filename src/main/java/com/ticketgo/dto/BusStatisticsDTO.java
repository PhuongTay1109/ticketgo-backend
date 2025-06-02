package com.ticketgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BusStatisticsDTO {
    private String licensePlate;
    private String busType;
    private Double totalRevenue;
    private Long totalBookings;
    private Long totalTicketsSold;
    private Double averageOccupancyRate;
}

