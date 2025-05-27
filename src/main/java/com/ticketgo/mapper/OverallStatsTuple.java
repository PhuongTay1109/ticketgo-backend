package com.ticketgo.mapper;

import java.math.BigDecimal;

public interface OverallStatsTuple {
    BigDecimal getTotalRevenue();
    Long getTotalTicketsSold();
    Integer getTotalBookings();
    Integer getTotalCancellations();
    Double getAverageTicketPrice();
}
