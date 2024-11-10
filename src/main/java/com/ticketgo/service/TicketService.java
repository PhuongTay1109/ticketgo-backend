package com.ticketgo.service;

import com.ticketgo.model.Customer;
import com.ticketgo.model.Ticket;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketService {
    void reserveSeats(long scheduleId, long seatId, long customerId);

    List<Ticket> findReservedTickets(long userId);

    void saveAll(List<Ticket> tickets);

    boolean existsReservedSeatsByCustomer(Customer customer);

    void releaseReservedSeatsByCustomer(long customerId);

    boolean isSeatAvailable( long seatId, long scheduleId);

    List<Ticket> findAllByScheduleId(long scheduleId);
}
