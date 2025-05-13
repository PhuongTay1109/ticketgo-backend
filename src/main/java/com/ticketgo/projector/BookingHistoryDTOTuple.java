package com.ticketgo.projector;

import java.time.LocalDateTime;

public interface BookingHistoryDTOTuple {
    Long getBookingId();
    LocalDateTime getBookingDate();
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
    String getOriginalPrice();
    String getDiscountedPrice();
    String getStatus();
}
