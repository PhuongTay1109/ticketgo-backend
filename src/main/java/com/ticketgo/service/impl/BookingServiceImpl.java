package com.ticketgo.service.impl;

import com.ticketgo.dto.*;
import com.ticketgo.enums.BookingStatus;
import com.ticketgo.enums.PaymentType;
import com.ticketgo.enums.TicketStatus;
import com.ticketgo.request.PaymentRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.response.TripInformationResponse;
import com.ticketgo.mapper.BookingHistoryMapper;
import com.ticketgo.mapper.BookingInfoMapper;
import com.ticketgo.entity.*;
import com.ticketgo.repository.BookingRepository;
import com.ticketgo.repository.PaymentRepository;
import com.ticketgo.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
    private final AuthenticationService authService;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

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

    @Override
    public List<BookingInfoDTO> getBookingInfoList(long bookingId) {
        List<BookingInfoDTOTuple> bookingInfoTuples = bookingRepo.findBookingInfoByBookingId(bookingId);

        return bookingInfoTuples.stream()
                .map(BookingInfoMapper::toBookingInfoDTO)
                .collect(Collectors.toList());
    }

    public ApiPaginationResponse getBookingHistoryForCustomer(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Customer customer = authService.getAuthorizedCustomer();
        Page<BookingHistoryDTOTuple> bookingHistoryPage =
                bookingRepo.getBookingHistoryForCustomer(customer.getUserId(), pageable);

        List<BookingHistoryDTO> bookingHistoryDTOs = bookingHistoryPage.getContent()
                .stream()
                .map(BookingHistoryMapper::toBookingHistoryDTO)
                .collect(Collectors.toList());

        ApiPaginationResponse.Pagination pagination = new ApiPaginationResponse.Pagination(
                bookingHistoryPage.getNumber() + 1,
                bookingHistoryPage.getSize(),
                bookingHistoryPage.getTotalPages(),
                bookingHistoryPage.getTotalElements()
        );

        return new ApiPaginationResponse(
                HttpStatus.OK,
                "Lịch sử đặt vé của khách hàng",
                bookingHistoryDTOs,
                pagination
        );
    }

    @Override
    public List<RevenueStatisticsDTO> getDailyRevenueStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        String dateFormat = "%Y-%m-%d";
        List<RevenueStatisticsDTOTuple> tuples =
                bookingRepo.getRevenueStatistics(dateFormat, startDate, endDate);

        return tuples.stream()
                .map(tuple -> new RevenueStatisticsDTO(
                        tuple.getPeriod(),
                        tuple.getTotalRevenue(),
                        tuple.getTotalTicketsSold()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<RevenueStatisticsDTO> getMonthlyRevenueStatistics(int year) {
        String dateFormat = "%Y-%m";
        LocalDateTime startDate =
                LocalDateTime.of(year, 1, 1, 0, 0, 0, 0);
        LocalDateTime endDate =
                LocalDateTime.of(year, 12, 31, 23, 59, 59, 999999);
        List<RevenueStatisticsDTOTuple> tuples =
                bookingRepo.getRevenueStatistics(dateFormat, startDate, endDate);

        return tuples.stream()
                .map(tuple -> new RevenueStatisticsDTO(
                        tuple.getPeriod(),
                        tuple.getTotalRevenue(),
                        tuple.getTotalTicketsSold()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<RevenueStatisticsDTO> getRevenueStatisticsByYear(int year) {
        String dateFormat = "%Y";
        LocalDateTime startDate =
                LocalDateTime.of(year, 1, 1, 0, 0, 0, 0);
        LocalDateTime endDate =
                LocalDateTime.of(year, 12, 31, 23, 59, 59, 999999);

        List<RevenueStatisticsDTOTuple> tuples =
                bookingRepo.getRevenueStatistics(dateFormat, startDate, endDate);

        return tuples.stream()
                .map(tuple -> new RevenueStatisticsDTO(
                        tuple.getPeriod(),
                        tuple.getTotalRevenue(),
                        tuple.getTotalTicketsSold()
                ))
                .collect(Collectors.toList());
    }
}
