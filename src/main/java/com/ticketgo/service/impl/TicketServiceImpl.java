package com.ticketgo.service.impl;

import com.ticketgo.exception.AppException;
import com.ticketgo.model.Customer;
import com.ticketgo.model.Ticket;
import com.ticketgo.model.TicketStatus;
import com.ticketgo.repository.TicketRepository;
import com.ticketgo.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepo;

    @Override
    @Transactional
    public void reserveSeats(long scheduleId, long seatId, long customerId) {
        Ticket ticket = ticketRepo.findTicketBySeatIdAndScheduleId(scheduleId, seatId);

        if (ticket == null || ticket.getStatus() != TicketStatus.AVAILABLE) {
            throw new AppException("Ghế này đã được đặt.", HttpStatus.CONFLICT);
        }

        ticketRepo.reserveSeats(scheduleId, seatId, customerId);
    }

    @Override
    public List<Ticket> findReservedTickets(long userId) {
        return ticketRepo.findReservedTicketsByCustomerId(userId);
    }

    @Override
    public void saveAll(List<Ticket> tickets) {
        ticketRepo.saveAll(tickets);
    }

    @Override
    public boolean existsReservedSeatsByCustomer(Customer customer) {
        return ticketRepo.existsReservedSeatsByCustomer(customer);
    }

    @Override
    public void releaseReservedSeatsByCustomer(long customerId) {
        ticketRepo.releaseReservedSeatsByCustomer(customerId);
    }

    @Override
    public boolean isSeatAvailable(long seatId, long scheduleId) {
        return ticketRepo.isSeatAvailable(seatId, scheduleId);
    }

    @Override
    public List<Ticket> findAllByScheduleId(long scheduleId) {
        return ticketRepo.findAllBySchedule_ScheduleId(scheduleId);
    }

    @Override
    public double getPriceBySeatIdAndScheduleId(long scheduleId, long seatId) {
        return ticketRepo.getPriceBySeatIdAndScheduleId(scheduleId, seatId);
    }

    @Override
    public Ticket findByTicketCode(String ticketCode) {
        return ticketRepo.findByTicketCode(ticketCode)
                .orElseThrow(() -> new RuntimeException("Ticket not found for ticket code " + ticketCode));
    }
}
