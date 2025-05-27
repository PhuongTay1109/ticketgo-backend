package com.ticketgo.projector;

import java.math.BigDecimal;

public interface RevenueStatisticsDTOTuple {
    String getPeriod();
    BigDecimal getTotalRevenue();
    Long getTotalTicketsSold();
}
