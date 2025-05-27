package com.ticketgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RouteStatisticsDTO {
    private String routeName;
    private BigDecimal totalRevenue;
    private Integer totalBookings;
    private Integer uniqueCustomers;
}