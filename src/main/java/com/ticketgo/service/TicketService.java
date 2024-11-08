package com.ticketgo.service;

public interface TicketService {
    void reserveSeats(long scheduleId, long seatId, long customerId);
}
