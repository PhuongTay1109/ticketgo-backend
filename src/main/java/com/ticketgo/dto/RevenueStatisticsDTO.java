package com.ticketgo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RevenueStatisticsDTO {
    private String period;
    private BigDecimal totalRevenue;
    private Long totalTicketsSold;
}
