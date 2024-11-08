package com.ticketgo.service;

import com.ticketgo.model.Ticket;

import java.util.List;

public interface TicketService {
    void reserveSeats(long scheduleId, long seatId, long customerId);

    List<Ticket> findReservedTickets(long userId);

    void saveAll(List<Ticket> tickets);
}
