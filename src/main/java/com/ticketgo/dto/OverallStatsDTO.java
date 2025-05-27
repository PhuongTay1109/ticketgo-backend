package com.ticketgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OverallStatsDTO {
    private BigDecimal totalRevenue;
    private Long totalTicketsSold;
    private Integer totalBookings;
    private Integer totalCancellations;
    private Double averageTicketPrice;
}
