package com.ticketgo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RevenueStatisticsDTO {
    private String period;
    private Double totalRevenue;
    private Long totalTicketsSold;
}
