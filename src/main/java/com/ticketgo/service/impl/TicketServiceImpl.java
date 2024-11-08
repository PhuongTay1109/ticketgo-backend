package com.ticketgo.service.impl;

import com.ticketgo.exception.AppException;
import com.ticketgo.model.Ticket;
import com.ticketgo.model.TicketStatus;
import com.ticketgo.repository.TicketRepository;
import com.ticketgo.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepo;

    @Override
    @Transactional
    public void reserveSeats(long scheduleId, long seatId, long customerId) {
        Ticket ticket = ticketRepo.selectTicketForUpdate(scheduleId, seatId);

        if (ticket == null || ticket.getStatus() != TicketStatus.AVAILABLE) {
            throw new AppException("The seat is already reserved or booked.", HttpStatus.BAD_REQUEST);
        }

        ticketRepo.reserveSeats(scheduleId, seatId, customerId);
    }
}
