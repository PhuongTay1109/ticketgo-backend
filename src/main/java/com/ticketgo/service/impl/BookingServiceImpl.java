package com.ticketgo.service.impl;

import com.ticketgo.model.*;
import com.ticketgo.repository.BookingRepository;
import com.ticketgo.repository.TicketRepository;
import com.ticketgo.service.BookingService;
import com.ticketgo.service.PaymentService;
import com.ticketgo.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepo;
    private final TicketService ticketService;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public void makeBooking() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        Customer customer = (Customer) authentication.getPrincipal();
        long customerId = customer.getUserId();

        List<Ticket> tickets = ticketService.findReservedTickets(customerId);

        double price = 0.0;

        for (Ticket ticket : tickets) {
            if(ticket.getStatus() == TicketStatus.REVERSED) {
                ticket.setStatus(TicketStatus.BOOKED);
                ticket.setReservedUntil(null);
                price += ticket.getPrice();
            }
        }

        ticketService.saveAll(tickets);
        Payment payment = Payment.builder()
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.COMPLETED)
                .type(PaymentType.CASH)
                .build();
        paymentService.save(payment);

        Booking booking = Booking.builder()
                .customer(customer)
                .bookingDate(LocalDateTime.now())
                .originalPrice(price)
                .payment(payment)
                .tickets(tickets)
                .build();

        bookingRepo.save(booking);

        for (Ticket ticket : tickets) {
            ticket.setBooking(booking);
        }

        ticketService.saveAll(tickets);
    }
}