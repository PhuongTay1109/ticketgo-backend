package com.ticketgo.service.impl;

import com.ticketgo.constant.BookingStep;
import com.ticketgo.constant.RedisKeys;
import com.ticketgo.dto.*;
import com.ticketgo.entity.*;
import com.ticketgo.enums.*;
import com.ticketgo.exception.AppException;
import com.ticketgo.mapper.*;
import com.ticketgo.projector.*;
import com.ticketgo.repository.*;
import com.ticketgo.request.*;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.response.PriceEstimationResponse;
import com.ticketgo.response.TripInformationResponse;
import com.ticketgo.service.*;
import com.ticketgo.util.DateTimeUtils;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final RefundRepository refundRepo;
    private final CanceledBookingHistoryRepository canceledBookingHistoryRepo;

    private final RedissonClient redisson;
    private final CustomerRepository customerRepository;

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
    public SavedInProgressInfo saveInProgressBooking(PaymentRequest request, long scheduleId) {
        long customerId = request.getCustomerId();
        Customer customer = customerService.findById(customerId);
        RouteStop pickupStop = routeStopService.findById(request.getPickupStopId());
        RouteStop dropoffStop = routeStopService.findById(request.getDropoffStopId());

        List<Ticket> tickets = ticketService.findReservedTickets(customerId, scheduleId);

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


        Customer customer = customerService.findById(booking.getCustomer().getUserId());

        double baseAmount = (booking.getDiscountedPrice() != null)
                ? booking.getDiscountedPrice()
                : booking.getOriginalPrice();

        double paidAmount = applyDiscount(baseAmount, customer.getLevel());

        booking.setDiscountedPrice(paidAmount);
        bookingRepo.save(booking);

        // 10.000 VND = 1 point
        int earnedPoints = (int) Math.floor(paidAmount / 10000);

        int totalPoints = customer.getPoints() + earnedPoints;
        customer.setPoints(totalPoints);

        if (totalPoints >= 500) {
            customer.setLevel(MembershipLevel.ELITE_EXPLORER);
        } else if (totalPoints >= 200) {
            customer.setLevel(MembershipLevel.GOLD_COMPANION);
        } else if (totalPoints >= 50) {
            customer.setLevel(MembershipLevel.LOYAL_TRAVELER);
        } else {
            customer.setLevel(MembershipLevel.NEW_PASSENGER);
        }

        customerService.save(customer);
    }

    private double applyDiscount(double amount, MembershipLevel level) {
        double discountRate = switch (level) {
            case NEW_PASSENGER -> 0.0;
            case LOYAL_TRAVELER -> 0.05;
            case GOLD_COMPANION -> 0.10;
            case ELITE_EXPLORER -> 0.15;
        };
        return Math.round(amount * (1 - discountRate));
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

        Map<Long, List<BookingHistoryDTOTuple>> groupedByBookingId = bookingHistoryPage.getContent().stream()
                .collect(Collectors.groupingBy(
                        BookingHistoryDTOTuple::getBookingId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));


        List<BookingHistoryDTO> bookingHistoryDTOs = groupedByBookingId.values().stream()
                .map(BookingHistoryMapper::toBookingHistoryDTO)
                .collect(Collectors.toList());

        List<CanceledBookingHistory> canceledBookingHistories = canceledBookingHistoryRepo.findAllByCustomerId(customer.getUserId());

        List<BookingHistoryDTO> canceledHistoryDTOs = canceledBookingHistories.stream()
                .map(booking -> {
                    Refund refund = refundRepo.findByBookingId(booking.getBookingId());

                    BookingHistoryDTO dto = new BookingHistoryDTO();
                    dto.setBookingId(booking.getBookingId());
                    dto.setBookingDate(booking.getBookingDate().format(DateTimeUtils.DATE_TIME_FORMATTER));
                    dto.setSeatInfos(booking.getSeatInfos());
                    dto.setContactName(booking.getContactName());
                    dto.setRouteName(booking.getRouteName());
                    dto.setDepartureDate(booking.getDepartureDate().format(DateTimeUtils.DATE_TIME_FORMATTER));
                    dto.setPickupTime(booking.getPickupTime().format(DateTimeUtils.DATE_TIME_FORMATTER));
                    dto.setPickupLocation(booking.getPickupLocation());
                    dto.setDropoffLocation(booking.getDropoffLocation());
                    dto.setLicensePlate(booking.getLicensePlate());
                    dto.setContactEmail(booking.getContactEmail());
                    dto.setOriginalPrice(String.valueOf(booking.getOriginalPrice()));
                    dto.setDiscountedPrice(String.valueOf(booking.getDiscountedPrice()));
                    dto.setStatus("Đã hủy");
                    if (refund.getRefundedAt() != null) {
                        dto.setRefundDate(refund.getRefundedAt().format(DateTimeUtils.DATE_TIME_FORMATTER));
                    } else {
                        dto.setRefundDate("");
                    }
                    dto.setRefundStatus(getRefundStatus(refund.getStatus()));
                    dto.setRefundAmount(String.valueOf(refund.getAmount()));
                    dto.setRefundReason(refund.getReason());
                    return dto;
                }).toList();

        List<BookingHistoryDTO> combinedHistory = new ArrayList<>();

        combinedHistory.addAll(bookingHistoryDTOs);
        combinedHistory.addAll(canceledHistoryDTOs);

        combinedHistory.sort((a, b) -> b.getBookingId().compareTo(a.getBookingId()));

        for (BookingHistoryDTO history : combinedHistory) {
            List<Ticket> tickets = ticketRepo.findAllByBooking_BookingId(history.getBookingId());

            if (tickets != null && !tickets.isEmpty()) {
                Driver driver = tickets.get(0).getSchedule().getDriver();
                if (driver != null) {
                    history.setDriverName(driver.getName());
                    history.setDriverPhone(driver.getPhoneNumber());
                } else {
                    history.setDriverName(null);
                    history.setDriverPhone(null);
                }
            } else {
                history.setDriverName(null);
                history.setDriverPhone(null);
            }
        }


        ApiPaginationResponse.Pagination pagination = new ApiPaginationResponse.Pagination(
                bookingHistoryPage.getNumber() + 1,
                bookingHistoryPage.getSize(),
                bookingHistoryPage.getTotalPages(),
                bookingHistoryPage.getTotalElements()
        );

        return new ApiPaginationResponse(
                HttpStatus.OK,
                "Lịch sử đặt vé của khách hàng",
                combinedHistory,
                pagination
        );
    }

    private static String getRefundStatus(RefundStatus status) {
        return switch (status) {
            case COMPLETED -> "Đã hoàn tiền";
            case PENDING  -> "Đang chờ hoàn tiền";
        };
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

    @Override
    @Transactional
    public void cancelBooking(CancelBookingRequest req) {
        long bookingId = req.getBookingId();
        Double amount = req.getAmount();
        String reason = req.getReason();

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        List<Ticket> tickets = ticketRepo.findAllByBooking_BookingId(bookingId);
        String seatInfo="";

        for (int i = 0; i < tickets.size(); i++) {
            seatInfo += tickets.get(i).getSeat().getSeatNumber();
            if (i < tickets.size() - 1) {
                seatInfo += ", ";
            }
        }

        // update CANCELLED status for bookings table
        bookingRepo.updateBookingStatusByBookingId(
                BookingStatus.CANCELLED,
                bookingId
        );

        // release tickets
        ticketRepo.cancelTicketsByBookingId(bookingId);

        // insert to refund table
        Refund refund = Refund.builder()
                .booking(booking)
                .amount(amount)
                .reason(reason)
                .status(RefundStatus.PENDING)
                .build();

        refundRepo.save(refund);

        // save to history table
        CanceledBookingHistory history = CanceledBookingHistory.builder()
                .bookingId(bookingId)
                .bookingDate(booking.getCreatedAt())
                .seatInfos(seatInfo)
                .contactName(booking.getContactName())
                .routeName(tickets.get(0).getSchedule().getRoute().getRouteName())
                .departureDate(tickets.get(0).getSchedule().getDepartureTime())
                .pickupTime(booking.getPickupStop().getArrivalTime())
                .pickupLocation(booking.getPickupStop().getLocation())
                .dropoffLocation(booking.getDropoffStop().getLocation())
                .licensePlate(tickets.get(0).getSeat().getBus().getLicensePlate())
                .contactEmail(booking.getContactEmail())
                .originalPrice(booking.getOriginalPrice())
                .discountedPrice(booking.getDiscountedPrice())
                .customerId(authService.getAuthorizedCustomer().getUserId())
                .build();

        canceledBookingHistoryRepo.save(history);
    }

    @Override
    public ApiPaginationResponse getAllBookingHistory(
            int pageNumber,
            int pageSize,
            Long bookingId,
            String contactName,
            String contactEmail,
            String routeName,
            String status,
            String refundStatus,
            String fromDate,
            String toDate
    ) {
        List<Long> customerIds = customerRepository.getAllCustomerId();
        List<BookingHistoryDTOTuple> bookingHistoryPage = bookingRepo.getAllBookingHistory(customerIds);

        Map<Long, List<BookingHistoryDTOTuple>> groupedByBookingId = bookingHistoryPage.stream()
                .collect(Collectors.groupingBy(
                        BookingHistoryDTOTuple::getBookingId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<BookingHistoryDTO> bookingHistoryDTOs = groupedByBookingId.values().stream()
                .map(BookingHistoryMapper::toBookingHistoryDTO)
                .collect(Collectors.toList());

        List<CanceledBookingHistory> canceledBookingHistories = canceledBookingHistoryRepo.findAllCustomer(customerIds);
        List<BookingHistoryDTO> canceledHistoryDTOs = canceledBookingHistories.stream()
                .map(booking -> {
                    Refund refund = refundRepo.findByBookingId(booking.getBookingId());

                    BookingHistoryDTO dto = new BookingHistoryDTO();
                    dto.setBookingId(booking.getBookingId());
                    dto.setBookingDate(booking.getBookingDate().format(DateTimeUtils.DATE_TIME_FORMATTER));
                    dto.setSeatInfos(booking.getSeatInfos());
                    dto.setContactName(booking.getContactName());
                    dto.setRouteName(booking.getRouteName());
                    dto.setDepartureDate(booking.getDepartureDate().format(DateTimeUtils.DATE_TIME_FORMATTER));
                    dto.setPickupTime(booking.getPickupTime().format(DateTimeUtils.DATE_TIME_FORMATTER));
                    dto.setPickupLocation(booking.getPickupLocation());
                    dto.setDropoffLocation(booking.getDropoffLocation());
                    dto.setLicensePlate(booking.getLicensePlate());
                    dto.setContactEmail(booking.getContactEmail());
                    dto.setOriginalPrice(String.valueOf(booking.getOriginalPrice()));
                    dto.setDiscountedPrice(String.valueOf(booking.getDiscountedPrice()));
                    dto.setStatus("Đã hủy");
                    if (refund.getRefundedAt() != null) {
                        dto.setRefundDate(refund.getRefundedAt().format(DateTimeUtils.DATE_TIME_FORMATTER));
                    } else {
                        dto.setRefundDate("");
                    }
                    dto.setRefundStatus(getRefundStatus(refund.getStatus()));
                    dto.setRefundAmount(String.valueOf(refund.getAmount()));
                    dto.setRefundReason(refund.getReason());
                    return dto;
                }).toList();

        List<BookingHistoryDTO> combinedHistory = new ArrayList<>();
        combinedHistory.addAll(bookingHistoryDTOs);
        combinedHistory.addAll(canceledHistoryDTOs);

        for (BookingHistoryDTO history : combinedHistory) {
            List<Ticket> tickets = ticketRepo.findAllByBooking_BookingId(history.getBookingId());

            if (tickets != null && !tickets.isEmpty()) {
                Driver driver = tickets.get(0).getSchedule().getDriver();
                if (driver != null) {
                    history.setDriverName(driver.getName());
                    history.setDriverPhone(driver.getPhoneNumber());
                } else {
                    history.setDriverName(null);
                    history.setDriverPhone(null);
                }
            } else {
                history.setDriverName(null);
                history.setDriverPhone(null);
            }
        }

        // --- Lọc dữ liệu theo các điều kiện search ---
        Stream<BookingHistoryDTO> filtered = combinedHistory.stream();

        if (bookingId != null) {
            filtered = filtered.filter(b -> b.getBookingId().equals(bookingId));
        }

        if (contactName != null && !contactName.isBlank()) {
            filtered = filtered.filter(b -> b.getContactName() != null && b.getContactName().toLowerCase().contains(contactName.toLowerCase()));
        }

        if (contactEmail != null && !contactEmail.isBlank()) {
            filtered = filtered.filter(b -> b.getContactEmail() != null && b.getContactEmail().toLowerCase().contains(contactEmail.toLowerCase()));
        }

        if (routeName != null && !routeName.isBlank()) {
            filtered = filtered.filter(b -> b.getRouteName() != null && b.getRouteName().toLowerCase().contains(routeName.toLowerCase()));
        }

        if (status != null && !status.isBlank()) {
            filtered = filtered.filter(b -> b.getStatus() != null && b.getStatus().equalsIgnoreCase(status));
        }

        if (refundStatus != null && !refundStatus.isBlank()) {
            filtered = filtered.filter(b -> refundStatus.equalsIgnoreCase(b.getRefundStatus()));
        }

        if (fromDate != null && toDate != null) {
            try {
                LocalDate from = LocalDate.parse(fromDate);
                LocalDate to = LocalDate.parse(toDate);

                filtered = filtered.filter(b -> {
                    LocalDate bookingDate = LocalDate.parse(b.getBookingDate(), DateTimeUtils.DATE_TIME_FORMATTER);
                    return (bookingDate.isEqual(from) || bookingDate.isAfter(from)) &&
                            (bookingDate.isEqual(to) || bookingDate.isBefore(to));
                });
            } catch (DateTimeParseException ignored) {}
        }

        List<BookingHistoryDTO> filteredList = filtered
                .sorted((a, b) -> b.getBookingId().compareTo(a.getBookingId()))
                .toList();

        // --- Phân trang ---
        int totalElements = filteredList.size();
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalElements);

        List<BookingHistoryDTO> pagedList = new ArrayList<>();
        if (fromIndex < totalElements) {
            pagedList = filteredList.subList(fromIndex, toIndex);
        }

        ApiPaginationResponse.Pagination pagination = new ApiPaginationResponse.Pagination(
                pageNumber,
                pageSize,
                totalPages,
                totalElements
        );

        return new ApiPaginationResponse(
                HttpStatus.OK,
                "Lịch sử đặt vé của khách hàng",
                pagedList,
                pagination
        );
    }

    @Override
    public void updateBookingRefundStatus(Long bookingsId) {
        Booking booking = bookingRepo.findById(bookingsId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.REFUNDED);
        bookingRepo.save(booking);

        Refund refund = refundRepo.findByBookingId(bookingsId);
        if (refund != null) {
            refund.setStatus(RefundStatus.COMPLETED);
            refund.setRefundedAt(LocalDateTime.now());
            refundRepo.save(refund);
        }
    }

    @Override
    public ComprehensiveStatisticsDTO getComprehensiveStatisticsDaily(LocalDateTime startDate, LocalDateTime endDate) {
        // Daily revenue
        String dateFormat = "%Y-%m-%d";
        List<RevenueStatisticsDTOTuple> dailyTuples = bookingRepo.getRevenueStatistics(dateFormat, startDate, endDate);
        List<RevenueStatisticsDTO> dailyRevenue = dailyTuples.stream()
                .map(t -> new RevenueStatisticsDTO(t.getPeriod(), t.getTotalRevenue(), t.getTotalTicketsSold()))
                .collect(Collectors.toList());

        // Route statistics
        List<RouteStatisticsTuple> routeTuples = bookingRepo.getRouteStatistics(startDate, endDate);
        List<RouteStatisticsDTO> routeStatistics = routeTuples.stream()
                .map(t -> new RouteStatisticsDTO(t.getRouteName(), t.getTotalRevenue(),
                        t.getTotalBookings(), t.getUniqueCustomers()))
                .collect(Collectors.toList());

        // Bus type statistics
        List<BusTypeStatisticsTuple> busTypeTuples = bookingRepo.getBusTypeStatistics(startDate, endDate);
        List<BusTypeStatisticsDTO.BusTypeStatItem> busTypeStats = busTypeTuples.stream()
                .map(t -> new BusTypeStatisticsDTO.BusTypeStatItem(
                        t.getBusType(),
                        t.getTotalRevenue(),
                        t.getTotalBookings(),
                        t.getAverageOccupancyRate()
                ))
                .collect(Collectors.toList());

        BusTypeStatisticsDTO busTypeStatistics = new BusTypeStatisticsDTO(busTypeStats);

        // Customer statistics
        CustomerStatisticsTuple customerTuple = bookingRepo.getCustomerStatistics(startDate, endDate);
        CustomerStatisticsDTO customerStatistics = new CustomerStatisticsDTO(
                customerTuple.getNewCustomers(),
                customerTuple.getReturningCustomers(),
                customerTuple.getAverageBookingsPerCustomer()
        );

        // Overall statistics
        OverallStatsTuple overallTuple = bookingRepo.getOverallStats(startDate, endDate);
        OverallStatsDTO overallStats = new OverallStatsDTO(
                overallTuple.getTotalRevenue(),
                overallTuple.getTotalTicketsSold(),
                overallTuple.getTotalBookings(),
                overallTuple.getTotalCancellations(),
                overallTuple.getAverageTicketPrice()
        );

        List<BusStatisticsTuple> busTuples = bookingRepo.getBusStatisticsByBus(startDate, endDate);
        List<BusStatisticsDTO> busStatistics = busTuples.stream()
                .map(t -> new BusStatisticsDTO(
                        t.getLicensePlate(),
                        t.getBusType(),
                        t.getTotalRevenue(),
                        t.getTotalBookings(),
                        t.getTotalTicketsSold(),
                        t.getAverageOccupancyRate()
                ))
                .collect(Collectors.toList());


        return new ComprehensiveStatisticsDTO(
                dailyRevenue,
                routeStatistics,
                busTypeStatistics,
                customerStatistics,
                overallStats,
                busStatistics
        );
    }

    @Override
    public ComprehensiveStatisticsDTO getComprehensiveStatisticsMonthly(int year) {
        String dateFormat = "%Y-%m";
        LocalDateTime startDate =
                LocalDateTime.of(year, 1, 1, 0, 0, 0, 0);
        LocalDateTime endDate =
                LocalDateTime.of(year, 12, 31, 23, 59, 59, 999999);

        List<RevenueStatisticsDTOTuple> dailyTuples = bookingRepo.getRevenueStatistics(dateFormat, startDate, endDate);
        List<RevenueStatisticsDTO> dailyRevenue = dailyTuples.stream()
                .map(t -> new RevenueStatisticsDTO(t.getPeriod(), t.getTotalRevenue(), t.getTotalTicketsSold()))
                .collect(Collectors.toList());

        // Route statistics
        List<RouteStatisticsTuple> routeTuples = bookingRepo.getRouteStatistics(startDate, endDate);
        List<RouteStatisticsDTO> routeStatistics = routeTuples.stream()
                .map(t -> new RouteStatisticsDTO(t.getRouteName(), t.getTotalRevenue(),
                        t.getTotalBookings(), t.getUniqueCustomers()))
                .collect(Collectors.toList());

        // Bus type statistics
        List<BusTypeStatisticsTuple> busTypeTuples = bookingRepo.getBusTypeStatistics(startDate, endDate);
        List<BusTypeStatisticsDTO.BusTypeStatItem> busTypeStats = busTypeTuples.stream()
                .map(t -> new BusTypeStatisticsDTO.BusTypeStatItem(
                        t.getBusType(),
                        t.getTotalRevenue(),
                        t.getTotalBookings(),
                        t.getAverageOccupancyRate()
                ))
                .collect(Collectors.toList());

        BusTypeStatisticsDTO busTypeStatistics = new BusTypeStatisticsDTO(busTypeStats);

        // Customer statistics
        CustomerStatisticsTuple customerTuple = bookingRepo.getCustomerStatistics(startDate, endDate);
        CustomerStatisticsDTO customerStatistics = new CustomerStatisticsDTO(
                customerTuple.getNewCustomers(),
                customerTuple.getReturningCustomers(),
                customerTuple.getAverageBookingsPerCustomer()
        );

        // Overall statistics
        OverallStatsTuple overallTuple = bookingRepo.getOverallStats(startDate, endDate);
        OverallStatsDTO overallStats = new OverallStatsDTO(
                overallTuple.getTotalRevenue(),
                overallTuple.getTotalTicketsSold(),
                overallTuple.getTotalBookings(),
                overallTuple.getTotalCancellations(),
                overallTuple.getAverageTicketPrice()
        );

        List<BusStatisticsTuple> busTuples = bookingRepo.getBusStatisticsByBus(startDate, endDate);
        List<BusStatisticsDTO> busStatistics = busTuples.stream()
                .map(t -> new BusStatisticsDTO(
                        t.getLicensePlate(),
                        t.getBusType(),
                        t.getTotalRevenue(),
                        t.getTotalBookings(),
                        t.getTotalTicketsSold(),
                        t.getAverageOccupancyRate()
                ))
                .collect(Collectors.toList());


        return new ComprehensiveStatisticsDTO(
                dailyRevenue,
                routeStatistics,
                busTypeStatistics,
                customerStatistics,
                overallStats,
                busStatistics
        );
    }

    @Override
    public Object getComprehensiveStatisticsYearly(int year) {
        String dateFormat = "%Y";
        LocalDateTime startDate =
                LocalDateTime.of(year, 1, 1, 0, 0, 0, 0);
        LocalDateTime endDate =
                LocalDateTime.of(year, 12, 31, 23, 59, 59, 999999);

        List<RevenueStatisticsDTOTuple> dailyTuples = bookingRepo.getRevenueStatistics(dateFormat, startDate, endDate);
        List<RevenueStatisticsDTO> dailyRevenue = dailyTuples.stream()
                .map(t -> new RevenueStatisticsDTO(t.getPeriod(), t.getTotalRevenue(), t.getTotalTicketsSold()))
                .collect(Collectors.toList());

        // Route statistics
        List<RouteStatisticsTuple> routeTuples = bookingRepo.getRouteStatistics(startDate, endDate);
        List<RouteStatisticsDTO> routeStatistics = routeTuples.stream()
                .map(t -> new RouteStatisticsDTO(t.getRouteName(), t.getTotalRevenue(),
                        t.getTotalBookings(), t.getUniqueCustomers()))
                .collect(Collectors.toList());

        // Bus type statistics
        List<BusTypeStatisticsTuple> busTypeTuples = bookingRepo.getBusTypeStatistics(startDate, endDate);
        List<BusTypeStatisticsDTO.BusTypeStatItem> busTypeStats = busTypeTuples.stream()
                .map(t -> new BusTypeStatisticsDTO.BusTypeStatItem(
                        t.getBusType(),
                        t.getTotalRevenue(),
                        t.getTotalBookings(),
                        t.getAverageOccupancyRate()
                ))
                .collect(Collectors.toList());

        BusTypeStatisticsDTO busTypeStatistics = new BusTypeStatisticsDTO(busTypeStats);

        // Customer statistics
        CustomerStatisticsTuple customerTuple = bookingRepo.getCustomerStatistics(startDate, endDate);
        CustomerStatisticsDTO customerStatistics = new CustomerStatisticsDTO(
                customerTuple.getNewCustomers(),
                customerTuple.getReturningCustomers(),
                customerTuple.getAverageBookingsPerCustomer()
        );

        // Overall statistics
        OverallStatsTuple overallTuple = bookingRepo.getOverallStats(startDate, endDate);
        OverallStatsDTO overallStats = new OverallStatsDTO(
                overallTuple.getTotalRevenue(),
                overallTuple.getTotalTicketsSold(),
                overallTuple.getTotalBookings(),
                overallTuple.getTotalCancellations(),
                overallTuple.getAverageTicketPrice()
        );

        List<BusStatisticsTuple> busTuples = bookingRepo.getBusStatisticsByBus(startDate, endDate);
        List<BusStatisticsDTO> busStatistics = busTuples.stream()
                .map(t -> new BusStatisticsDTO(
                        t.getLicensePlate(),
                        t.getBusType(),
                        t.getTotalRevenue(),
                        t.getTotalBookings(),
                        t.getTotalTicketsSold(),
                        t.getAverageOccupancyRate()
                ))
                .collect(Collectors.toList());


        return new ComprehensiveStatisticsDTO(
                dailyRevenue,
                routeStatistics,
                busTypeStatistics,
                customerStatistics,
                overallStats,
                busStatistics
        );
    }
}
