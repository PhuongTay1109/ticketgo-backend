package com.ticketgo.mapper;

import java.math.BigDecimal;

public interface RouteStatisticsTuple {
    String getRouteName();
    BigDecimal getTotalRevenue();
    Integer getTotalBookings();
    Integer getUniqueCustomers();
}

