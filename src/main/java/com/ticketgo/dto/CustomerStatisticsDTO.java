package com.ticketgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerStatisticsDTO {
    private Integer newCustomers;
    private Integer returningCustomers;
    private Double averageBookingsPerCustomer;
}

