package com.ticketgo.projector;

public interface BusStatisticsTuple {
    String getLicensePlate();
    String getBusType();
    Double getTotalRevenue();
    Long getTotalBookings();
    Long getTotalTicketsSold();
    Double getAverageOccupancyRate();
}

