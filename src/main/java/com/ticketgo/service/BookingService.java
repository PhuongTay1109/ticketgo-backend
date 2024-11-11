package com.ticketgo.service;

import com.ticketgo.dto.request.BookingRequest;

public interface BookingService {
    void saveBookingForVNPay(BookingRequest request);
}
