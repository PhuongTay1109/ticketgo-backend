package com.ticketgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ComprehensiveStatisticsDTO {
    private List<RevenueStatisticsDTO> revenue;
    private List<RouteStatisticsDTO> routeStatistics;
    private BusTypeStatisticsDTO busTypeStatistics;
    private CustomerStatisticsDTO customerStatistics;
    private OverallStatsDTO overallStats;
    private List<BusStatisticsDTO> busStatistics;
}

