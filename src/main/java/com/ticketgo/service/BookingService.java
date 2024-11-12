package com.ticketgo.service;

import com.ticketgo.dto.request.BookingRequest;
import com.ticketgo.dto.response.TripInformationResponse;

public interface BookingService {
    void saveBookingForVNPay(BookingRequest request);
    TripInformationResponse getTripInformation(long pickupStopId, long dropoffStopId, long scheduleId);
}
