package com.ticketgo.service.impl;

import com.ticketgo.constant.BookingStep;
import com.ticketgo.constant.RedisKeys;
import com.ticketgo.dto.*;
import com.ticketgo.entity.*;
import com.ticketgo.enums.BookingStatus;
import com.ticketgo.enums.PaymentType;
import com.ticketgo.enums.TicketStatus;
import com.ticketgo.exception.AppException;
import com.ticketgo.mapper.BookingHistoryMapper;
import com.ticketgo.mapper.BookingInfoMapper;
import com.ticketgo.projector.BookingHistoryDTOTuple;
import com.ticketgo.projector.BookingInfoDTOTuple;
import com.ticketgo.projector.CustomerInfoDTOTuple;
import com.ticketgo.projector.RevenueStatisticsDTOTuple;
import com.ticketgo.repository.BookingRepository;
import com.ticketgo.repository.PaymentRepository;
import com.ticketgo.repository.PromotionRepository;
import com.ticketgo.repository.TicketRepository;
import com.ticketgo.request.PaymentRequest;
import com.ticketgo.request.PriceEstimationRequest;
import com.ticketgo.request.SaveBookingInfoRequest;
import com.ticketgo.request.SaveContactInfoRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.response.PriceEstimationResponse;
import com.ticketgo.response.TripInformationResponse;
import com.ticketgo.service.*;
import com.ticketgo.util.GsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    private final SeatService seatService;
    private final TicketRepository ticketRepo;
    private final PromotionRepository promotionRepo;

    private final RedissonClient redisson;

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
                .pickupId(pickupStopId)
                .dropoffId(dropoffStopId)
                .build();
    }

    @Override
    @Transactional
    public SavedInProgressInfo saveInProgressBooking(PaymentRequest request) {
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

        Double totalPrice = Double.valueOf(request.getTotalPrice());
        Double discountedPrice = totalPrice;
        Promotion promotion = null;

        if (request.getPromotionId() != null) {
            promotion = promotionRepo.findByPromotionId(request.getPromotionId());
            if (promotion == null) {
                throw new AppException("Promotion not found", HttpStatus.NOT_FOUND);
            }
            int discountedAmount = promotion.getDiscountPercentage();
            discountedPrice = totalPrice - (totalPrice * discountedAmount / 100);
        }

        Booking booking = Booking.builder()
                .customer(customer)
                .bookingDate(LocalDateTime.now())
                .originalPrice(totalPrice)
                .discountedPrice(discountedPrice)
                .tickets(tickets)
                .contactEmail(request.getContactEmail())
                .contactName(request.getContactName())
                .contactPhone(request.getContactPhone())
                .pickupStop(pickupStop)
                .dropoffStop(dropoffStop)
                .status(BookingStatus.IN_PROGRESS)
                .promotion(promotion)
                .build();

        bookingRepo.save(booking);

        for (Ticket ticket : tickets) {
            ticket.setBooking(booking);
        }

        ticketService.saveAll(tickets);

        return new SavedInProgressInfo(booking.getBookingId(), discountedPrice);
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

    @Override
    @Transactional
    public void saveBookingInfo(SaveBookingInfoRequest request) {
        Customer customer = authService.getAuthorizedCustomer();
        long customerId = customer.getUserId();
        log.info("Processing booking confirmation for customerId: {}", customerId);

        PriceEstimationRequest priceReq = new PriceEstimationRequest(request.getTicketCodes());
        PriceEstimationResponse prices = seatService.getSeatPrice(priceReq);
        log.info("Retrieved price estimation: {}", GsonUtils.toJson(prices));

        TripInformationResponse tripInfo = getTripInformation(
                request.getPickupStopId(),
                request.getDropoffStopId(),
                request.getScheduleId()
        );
        log.info("Retrieved trip information: {}", GsonUtils.toJson(tripInfo));

        BookingConfirmDTO bookingConfirm = new BookingConfirmDTO(prices, tripInfo);
        log.debug("Constructed BookingConfirmDTO: {}", GsonUtils.toJson(bookingConfirm));

        String key = RedisKeys.userBookingInfoKey(customerId, request.getScheduleId());
        log.info("Generated Redis key: {}", key);

        RBucket<String> bookingBucket = redisson.getBucket(key);

        if (bookingBucket.isExists()) {
            log.info("Booking info already exists for key: {}. Deleting old data...", key);
            bookingBucket.delete();
        }

        bookingBucket.set(GsonUtils.toJson(bookingConfirm), 30, TimeUnit.MINUTES);
        log.info("Saved booking confirmation to Redis with key: {}", key);
    }

    @Override
    public BookingConfirmDTO getBookingInfo(Long scheduleId) {
        Customer customer = authService.getAuthorizedCustomer();
        long customerId = customer.getUserId();
        log.info("Fetching booking confirmation for customerId: {} and scheduleId: {}", customerId, scheduleId);

        String key = RedisKeys.userBookingInfoKey(customerId, scheduleId);
        String json = (String) redisson.getBucket(key).get();

        if (json == null) {
            log.warn("No booking confirmation found for key: {}", key);
            throw new AppException("Booking confirmation not found", HttpStatus.NOT_FOUND);
        }

        log.info("Retrieved booking confirmation from Redis for key: {}", key);
        return GsonUtils.fromJson(json, BookingConfirmDTO.class);
    }

    @Override
    public BookingStepDTO getBookingStep(Long scheduleId) {
        Customer customer = authService.getAuthorizedCustomer();
        long customerId = customer.getUserId();
        log.info("Fetching booking step for customerId: {}, scheduleId: {}", customerId, scheduleId);

        String vnPayUrlKey = RedisKeys.vnPayUrlKey(customerId, scheduleId);
        RBucket<String> paymentBucket = redisson.getBucket(vnPayUrlKey);
        if (paymentBucket.isExists()) {
            log.info("Payment found in Redis for customerId: {}, scheduleId: {}", customerId, scheduleId);
            return new BookingStepDTO(BookingStep.PAYMENT.getStep(), paymentBucket.get());
        }

        boolean existedReservedSeat = ticketRepo.existsReservedSeatsByCustomer(customer);
        log.info("Reserved seat exists for customerId: {}: {}", customerId, existedReservedSeat);
        if (existedReservedSeat) {
            return new BookingStepDTO(BookingStep.HOLD_TICKET.getStep());
        }

        log.info("No reserved seat or payment found. Returning SELECT_SEAT step.");
        return new BookingStepDTO(BookingStep.SELECT_SEAT.getStep());
    }

    @Override
    public void saveCustomerContactInfo(SaveContactInfoRequest request) {
        Customer customer = authService.getAuthorizedCustomer();
        long customerId = customer.getUserId();

        String contactInfoKey = RedisKeys.contactInfoKey(customerId, request.getScheduleId());
        RBucket<String> contactInfo = redisson.getBucket(contactInfoKey);

        if (contactInfo.isExists()) {
            log.info("Contact info already exists for key: {}. Deleting old data...", contactInfoKey);
            contactInfo.delete();
        }

        contactInfo.set(GsonUtils.toJson(request), 30, TimeUnit.MINUTES);
    }

    @Override
    public SaveContactInfoRequest getCustomerContactInfo(long scheduleId) {
        Customer customer = authService.getAuthorizedCustomer();
        long customerId = customer.getUserId();

        String contactInfoKey = RedisKeys.contactInfoKey(customerId, scheduleId);
        String json = (String) redisson.getBucket(contactInfoKey).get();
        return GsonUtils.fromJson(json, SaveContactInfoRequest.class);
    }

    @Override
    public List<CustomerInfoDTOTuple> getPassengerInfoByScheduleId(Long scheduleId) {
        return bookingRepo.getPassengerInfoByScheduleId(scheduleId);
    }
}
