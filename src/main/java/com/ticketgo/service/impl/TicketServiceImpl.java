package com.ticketgo.service.impl;

import com.ticketgo.dto.BookingRequestDTO;
import com.ticketgo.entity.*;
import com.ticketgo.enums.BookingStatus;
import com.ticketgo.enums.PaymentType;
import com.ticketgo.enums.TicketStatus;
import com.ticketgo.exception.AppException;
import com.ticketgo.repository.BookingRepository;
import com.ticketgo.repository.PaymentRepository;
import com.ticketgo.repository.RouteStopRepository;
import com.ticketgo.repository.TicketRepository;
import com.ticketgo.service.AuthenticationService;
import com.ticketgo.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final PaymentRepository paymentRepository;
    private final RouteStopRepository routeStopRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public void reserveSeats(String ticketCode, long customerId) {
        ticketRepo.reserveSeats(ticketCode, customerId);
    }

    @Override
    public List<Ticket> findReservedTickets(long userId, long scheduleId) {
        return ticketRepo.findReservedTicketsByCustomerId(userId, scheduleId);
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

    @Override
    public void reserveSeats(List<String> ticketCodes) {
        ticketRepo.reserveSeatsByTicketCodes(ticketCodes);
    }

    @Override
    public void adminBooking(BookingRequestDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        RouteStop pickupStop = routeStopRepository.findById(dto.getPickupStopId())
                .orElseThrow(() -> new AppException("Pickup stop not found", HttpStatus.NOT_FOUND));

        RouteStop dropoffStop = routeStopRepository.findById(dto.getDropoffStopId())
                .orElseThrow(() -> new AppException("Dropoff stop not found", HttpStatus.NOT_FOUND));

        Payment payment = new Payment();
        payment.setPaymentDate(LocalDateTime.now());
        payment.setType(PaymentType.CASH);
        payment.setStatus(dto.getPaymentStatus());
        paymentRepository.save(payment);

        Booking booking = new Booking();
        booking.setBookingDate(LocalDateTime.now());
        booking.setContactName(dto.getContactName());
        booking.setContactEmail(dto.getContactEmail());
        booking.setContactPhone(dto.getContactPhone());
        booking.setOriginalPrice(dto.getPrice());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCustomer(user);
        booking.setPickupStop(pickupStop);
        booking.setDropoffStop(dropoffStop);
        booking.setPayment(payment);
        bookingRepository.save(booking);

        List<Ticket> tickets = ticketRepo.findAllByTicketCodeIn(dto.getTicketCodes());
        for (Ticket ticket : tickets) {
            ticket.setBooking(booking);
            ticket.setStatus(TicketStatus.BOOKED);
            ticket.setReservedUntil(null);
        }
        ticketRepo.saveAll(tickets);
    }
}
