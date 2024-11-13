package com.ticketgo.dto;

import java.time.LocalDateTime;

public interface BookingInfoDTOTuple {
    String getTicketCode();
    String getContactName();
    String getRouteName();
    LocalDateTime getDepartureDate();
    LocalDateTime getPickupTime();
    String getPickupLocation();
    String getDropoffLocation();
    String getSeatNumber();
    String getLicensePlate();
    String getContactEmail();
    Double getPrice();
}
