package com.ticketgo.service.impl;

import com.ticketgo.dto.request.PaymentRequest;
import com.ticketgo.dto.response.TripInformationResponse;
import com.ticketgo.model.*;
import com.ticketgo.repository.BookingRepository;
import com.ticketgo.repository.PaymentRepository;
import com.ticketgo.service.*;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepo;
    private final TicketService ticketService;
    private final CustomerService customerService;
    private final RouteStopService routeStopService;
    private final ScheduleService scheduleService;
    private final BusService busService;
    private final PaymentRepository paymentRepo;

    @Override
    @Transactional
    public TripInformationResponse getTripInformation(long pickupStopId, long dropoffStopId, long scheduleId) {
        Schedule schedule = scheduleService.findById(scheduleId);
        Bus bus = busService.findBySchedule(scheduleId);
        RouteStop pickupStop = routeStopService.findById(pickupStopId);
        RouteStop dropoffStop = routeStopService.findById(dropoffStopId);

        return TripInformationResponse.builder()
                .departureTime(schedule.getDepartureTime())
                .licensePlate(bus.getLicensePlate())
                .busType(bus.getBusType())
                .pickupTime(pickupStop.getArrivalTime())
                .pickupLocation(pickupStop.getLocation())
                .dropoffTime(dropoffStop.getArrivalTime())
                .dropoffLocation(dropoffStop.getLocation())
                .build();
    }

    @Override
    @Transactional
    public long saveInProgressBooking(PaymentRequest request) {
        long customerId = request.getCustomerId();
        Customer customer = customerService.findById(customerId);
        RouteStop pickupStop = routeStopService.findById(request.getPickupStopId());
        RouteStop dropoffStop = routeStopService.findById(request.getDropoffStopId());

        List<Ticket> tickets = ticketService.findReservedTickets(customerId);

        for (Ticket ticket : tickets) {
            log.info("ticket status {} and seat id {}", ticket.getStatus(), ticket.getSeat().getSeatId());
            ticket.setStatus(TicketStatus.BOOKED);
            ticket.setReservedUntil(null);
        }
        ticketService.saveAll(tickets);

        Booking booking = Booking.builder()
                .customer(customer)
                .bookingDate(LocalDateTime.now())
                .originalPrice(Double.valueOf(request.getTotalPrice()))
                .tickets(tickets)
                .contactEmail(request.getContactEmail())
                .contactName(request.getContactName())
                .contactPhone(request.getContactPhone())
                .pickupStop(pickupStop)
                .dropoffStop(dropoffStop)
                .status(BookingStatus.IN_PROGRESS)
                .build();

        bookingRepo.save(booking);

        for (Ticket ticket : tickets) {
            ticket.setBooking(booking);
        }

        ticketService.saveAll(tickets);

        return booking.getBookingId();
    }

    @Override
    @Transactional
    public void setConfirmedVNPayBooking(long bookingId) {
        Payment payment = Payment.builder()
                .paymentDate(LocalDateTime.now())
                .type(PaymentType.VNPAY)
                .build();
        paymentRepo.save(payment);

        Booking booking = findById(bookingId);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPayment(payment);
        bookingRepo.save(booking);
    }

    @Override
    @Transactional
    public void setFailedVNPayBooking(long bookingId) {
        List<Ticket> tickets = ticketService.findAllByBookingId(bookingId);
        for(Ticket ticket : tickets) {
            ticket.setStatus(TicketStatus.AVAILABLE);
            ticket.setCustomer(null);
            ticket.setReservedUntil(null);
        }
        ticketService.saveAll(tickets);

        Payment payment = Payment.builder()
                .paymentDate(LocalDateTime.now())
                .type(PaymentType.VNPAY)
                .build();
        paymentRepo.save(payment);

        Booking booking = findById(bookingId);
        booking.setStatus(BookingStatus.FAILED);
        booking.setPayment(payment);
        bookingRepo.save(booking);
    }

    @Override
    public Booking findById(long bookingId) {
        return bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found for id " + bookingId));
    }
}
