package com.ticketgo.mapper;

import java.math.BigDecimal;

public interface BusTypeStatisticsTuple {
    String getBusType();
    BigDecimal getTotalRevenue();
    Integer getTotalBookings();
    Double getAverageOccupancyRate();
}
