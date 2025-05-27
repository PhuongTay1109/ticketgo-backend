package com.ticketgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class BusTypeStatisticsDTO {
    private List<BusTypeStatItem> stats;

    @Data
    @AllArgsConstructor
    public static class BusTypeStatItem {
        private String busType;
        private BigDecimal totalRevenue;
        private Integer totalBookings;
        private Double averageOccupancyRate;
    }
}