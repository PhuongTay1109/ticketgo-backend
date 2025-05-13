package com.ticketgo.service;

import com.ticketgo.entity.Ticket;

import java.util.List;

public interface TicketService {
    void reserveSeats(String ticketCode, long customerId);

    List<Ticket> findReservedTickets(long userId);

    void saveAll(List<Ticket> tickets);

    boolean existsReservedSeatsByCustomer();

    void releaseReservedSeatsByCustomer(long customerId, long scheduleId);

    boolean isSeatAvailable( long seatId, long scheduleId);

    List<Ticket> findAllByScheduleId(long scheduleId);

    double getPriceBySeatIdAndScheduleId(long scheduleId, long seatId);

    Ticket findByTicketCode(String ticketCode);

    List<Ticket> findAllByBookingId(long bookingId);

    Long getTicketRemainingTime(String ticketCode);
}
