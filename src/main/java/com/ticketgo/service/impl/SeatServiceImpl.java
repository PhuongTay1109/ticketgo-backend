package com.ticketgo.service.impl;

import com.ticketgo.constant.RedisKeys;
import com.ticketgo.dto.BookingConfirmDTO;
import com.ticketgo.dto.SeatDTO;
import com.ticketgo.entity.Bus;
import com.ticketgo.entity.Customer;
import com.ticketgo.entity.Seat;
import com.ticketgo.entity.Ticket;
import com.ticketgo.enums.TicketStatus;
import com.ticketgo.exception.AppException;
import com.ticketgo.repository.SeatRepository;
import com.ticketgo.request.PriceEstimationRequest;
import com.ticketgo.request.SeatReservationRequest;
import com.ticketgo.response.PriceEstimationResponse;
import com.ticketgo.service.AuthenticationService;
import com.ticketgo.service.BusService;
import com.ticketgo.service.SeatService;
import com.ticketgo.service.TicketService;
import com.ticketgo.util.GsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepo;
    private final TicketService ticketService;
    private final BusService busService;
    private final AuthenticationService authService;
    private final RedissonClient redisson;

    @Override
    public Map<String, List<List<SeatDTO>>> getSeatStatusForSchedule(Long scheduleId) {
        Bus bus = busService.findBySchedule(scheduleId);
        Set<Seat> seats = bus.getSeats();

        List<Seat> sortedSeats = new ArrayList<>(seats);
        sortedSeats.sort(Comparator.comparingInt(Seat::getFloor)
                .thenComparingInt(Seat::getRow)
                .thenComparing(Seat::getCol));

        // floor_, data
        Map<String, List<List<SeatDTO>>> result = new LinkedHashMap<>();
        //example
//        {
//            1: { // Tầng 1
//            1: [SeatDTO_A1], // Hàng 1: chứa Ghế A1
//            2: [SeatDTO_B2]  // Hàng 2: chứa Ghế B2
//        },
//            2: { // Tầng 2
//            1: [SeatDTO_C1]  // Hàng 1: chứa Ghế C1
//        }
//        }
        Map<Integer, Map<Integer, List<SeatDTO>>> floorGroups = new LinkedHashMap<>();
        Map<Long, String> seatTicketMap = new HashMap<>();
        List<Ticket> tickets = ticketService.findAllByScheduleId(scheduleId);

        Map<Long, TicketStatus> seatStatusMap = new HashMap<>();
        for (Ticket ticket : tickets) {
            long seatId = ticket.getSeat().getSeatId();
            seatStatusMap.put(seatId, ticket.getStatus());
            seatTicketMap.put(seatId, ticket.getTicketCode());
        }

        for (Seat seat : sortedSeats) {
            long seatId = seat.getSeatId();
            TicketStatus status = seatStatusMap.get(seatId);
            String ticketCode = seatTicketMap.get(seatId);

            SeatDTO seatDTO = new SeatDTO(
                    ticketCode,
                    seat.getSeatNumber(),
                    status == TicketStatus.AVAILABLE
            );

            floorGroups
                    .computeIfAbsent(seat.getFloor(), k -> new LinkedHashMap<>())
                    .computeIfAbsent(seat.getRow(), k -> new ArrayList<>())
                    .add(seatDTO);
        }

        for (Map.Entry<Integer, Map<Integer, List<SeatDTO>>> floorEntry : floorGroups.entrySet()) {
            String floorKey = "floor_" + floorEntry.getKey();
            List<List<SeatDTO>> rows = new ArrayList<>();

            for (Map.Entry<Integer, List<SeatDTO>> rowEntry : floorEntry.getValue().entrySet()) {
                rows.add(rowEntry.getValue());
            }

            result.put(floorKey, rows);
        }

        return result;
    }

    @Override
    public int countAvailableSeatsBySchedule(Long scheduleId) {
        return seatRepo.countAvailableSeatsByScheduleId(scheduleId);
    }

    @Override
    @Transactional
    public void reserveSeats(SeatReservationRequest request) {
        Customer customer = authService.getAuthorizedCustomer();
        long customerId = customer.getUserId();

        List<String> ticketCodes = new ArrayList<>();

        // Xử lý chiều đi
        if (request.getScheduleId() != null) {
            String key = RedisKeys.userBookingInfoKey(customerId, request.getScheduleId());
            String json = (String) redisson.getBucket(key).get();

            if (json == null) {
                log.warn("No booking confirmation found for key: {}", key);
                throw new AppException("Booking confirmation not found for scheduleId: " + request.getScheduleId(), HttpStatus.NOT_FOUND);
            }

            log.info("Retrieved booking confirmation from Redis for key: {}", key);
            BookingConfirmDTO bookingInfo = GsonUtils.fromJson(json, BookingConfirmDTO.class);

            List<String> departTicketCodes = bookingInfo.getPrices().getSeatNumbers()
                    .stream()
                    .map(seatNumber -> String.format("TICKET-%d-%s", request.getScheduleId(), seatNumber))
                    .collect(Collectors.toList());

            ticketCodes.addAll(departTicketCodes);
        }

        // Xử lý chiều về (returnScheduleId)
        if (request.getReturnScheduleId() != null) {
            String returnKey = RedisKeys.userBookingInfoKey(customerId, request.getReturnScheduleId());
            String returnJson = (String) redisson.getBucket(returnKey).get();

            if (returnJson == null) {
                log.warn("No booking confirmation found for return key: {}", returnKey);
                throw new AppException("Booking confirmation not found for returnScheduleId: " + request.getReturnScheduleId(), HttpStatus.NOT_FOUND);
            }

            log.info("Retrieved booking confirmation from Redis for return key: {}", returnKey);
            BookingConfirmDTO returnBookingInfo = GsonUtils.fromJson(returnJson, BookingConfirmDTO.class);

            List<String> returnTicketCodes = returnBookingInfo.getPrices().getSeatNumbers()
                    .stream()
                    .map(seatNumber -> String.format("TICKET-%d-%s", request.getReturnScheduleId(), seatNumber))
                    .collect(Collectors.toList());

            ticketCodes.addAll(returnTicketCodes);
        }

        // Nếu không có scheduleId thì lấy ticketCodes từ request trực tiếp
        if (request.getScheduleId() == null && request.getReturnScheduleId() == null) {
            ticketCodes = request.getTicketCodes();
        }

        // Kiểm tra trạng thái vé còn trống
        for (String ticketCode : ticketCodes) {
            Ticket ticket = ticketService.findByTicketCode(ticketCode);
            if (ticket.getStatus() != TicketStatus.AVAILABLE) {
                throw new AppException(
                        "Chỗ bạn chọn đã có người khác nhanh tay mua rồi, bạn hãy chọn chỗ khác nhé.",
                        HttpStatus.BAD_REQUEST);
            }
        }

        // Đặt giữ chỗ cho tất cả các ticket
        for (String ticketCode : ticketCodes) {
            ticketService.reserveSeats(ticketCode, customerId);
        }
    }

    @Override
    public void cancelReservedSeatsByCustomer(long scheduleId, Long returnScheduleId) {
        Customer customer = authService.getAuthorizedCustomer();
        long customerId = customer.getUserId();
        ticketService.releaseReservedSeatsByCustomer(customerId, scheduleId);
        if (returnScheduleId != null && returnScheduleId != 0) {
            ticketService.releaseReservedSeatsByCustomer(customerId, returnScheduleId);
        }
    }

    @Override
    public PriceEstimationResponse getSeatPrice(PriceEstimationRequest request) {
        List<String> ticketCodes = request.getTicketCodes();

        double totalPrice = 0;
        double unitPrice = 0;

        List<String> seatNumbers = new ArrayList<>();

        for (String ticketCode : ticketCodes) {
            Ticket ticket = ticketService.findByTicketCode(ticketCode);
            totalPrice += ticket.getPrice();
            seatNumbers.add(ticket.getSeat().getSeatNumber());
            if (unitPrice == 0) {
                unitPrice = ticket.getPrice();
            }
        }

        return PriceEstimationResponse.builder()
                .seatNumbers(seatNumbers)
                .unitPrice(unitPrice)
                .quantity(ticketCodes.size())
                .totalPrice(totalPrice)
                .build();
    }

    public Seat findById(long id) {
        return seatRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat with id " + id + " not found"));
    }
}
