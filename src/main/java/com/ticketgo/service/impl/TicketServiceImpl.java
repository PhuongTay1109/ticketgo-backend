package com.ticketgo.service.impl;

import com.ticketgo.entity.Customer;
import com.ticketgo.entity.Ticket;
import com.ticketgo.repository.TicketRepository;
import com.ticketgo.service.AuthenticationService;
import com.ticketgo.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepo;
    private final AuthenticationService authService;

    @Override
    @Transactional
    public void reserveSeats(String ticketCode, long customerId) {
        ticketRepo.reserveSeats(ticketCode, customerId);
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
    public boolean existsReservedSeatsByCustomer() {
        Customer customer = authService.getAuthorizedCustomer();
        return ticketRepo.existsReservedSeatsByCustomer(customer);
    }

    @Override
    public void releaseReservedSeatsByCustomer(long customerId, long scheduleId) {
        ticketRepo.releaseReservedSeatsByCustomer(customerId, scheduleId);
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

    @Override
    public List<Ticket> findAllByBookingId(long bookingId) {
        return ticketRepo.findAllByBooking_BookingId(bookingId);
    }

    @Override
    public Long getTicketRemainingTime(String ticketCode) {
        Ticket ticket = findByTicketCode(ticketCode);
        if (ticket.getReservedUntil() == null) {
            return 0L;
        }
        return Duration.between(LocalDateTime.now(), ticket.getReservedUntil()).toSeconds();
    }
}
