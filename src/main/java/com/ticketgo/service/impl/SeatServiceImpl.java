package com.ticketgo.service.impl;

import com.ticketgo.dto.SeatDTO;
import com.ticketgo.dto.request.SeatReservationRequest;
import com.ticketgo.dto.request.TotalPriceCalculationRequest;
import com.ticketgo.dto.response.TotalPriceCalculationResponse;
import com.ticketgo.exception.AppException;
import com.ticketgo.model.*;
import com.ticketgo.repository.SeatRepository;
import com.ticketgo.service.AuthenticationService;
import com.ticketgo.service.BusService;
import com.ticketgo.service.SeatService;

import com.ticketgo.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepo;
    private final TicketService ticketService;
    private final BusService busService;
    private final AuthenticationService authService;

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

        Map<Long, TicketStatus> seatStatusMap = getTicketStatusBySchedule(scheduleId);

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

        for (Seat seat : sortedSeats) {
            TicketStatus status = seatStatusMap.get(seat.getSeatId());

            SeatDTO seatDTO = new SeatDTO(
                    seat.getSeatId(),
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

    private Map<Long, TicketStatus> getTicketStatusBySchedule(Long scheduleId) {
        List<Ticket> tickets = ticketService.findAllByScheduleId(scheduleId);

        Map<Long, TicketStatus> seatStatusMap = new HashMap<>();
        for (Ticket ticket : tickets) {
            seatStatusMap.put(
                            ticket.getSeat().getSeatId(),
                            ticket.getStatus());
        }

        return seatStatusMap;
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

        if (ticketService.existsReservedSeatsByCustomer(customer)) {
            throw new AppException(
                            "Bạn có đặt chỗ chưa hoàn tất. ",
                            HttpStatus.CONFLICT);
        }

        List<Long> seatIds = request.getSeatIds();
        long scheduleId = request.getScheduleId();

        for(long seatId : seatIds) {
            ticketService.reserveSeats(scheduleId, seatId, customerId);
        }
    }

    @Override
    public void releaseReservedSeatsByCustomer() {
        Customer customer = authService.getAuthorizedCustomer();
        long customerId = customer.getUserId();
        ticketService.releaseReservedSeatsByCustomer(customerId);
    }

    @Override
    public TotalPriceCalculationResponse getSeatPrice(TotalPriceCalculationRequest request) {
        List<Long> seatIds = request.getSeatIds();
        long scheduleId = request.getScheduleId();
        List<String> seatNumbers = new ArrayList<>();

        double totalPrice = 0;
        double unitPrice = 0;

        for(long seatId : seatIds) {
            seatNumbers.add(findById(seatId).getSeatNumber());
            totalPrice += ticketService.getPriceBySeatIdAndScheduleId(scheduleId, seatId);
            if(unitPrice == 0) {
                unitPrice = ticketService.getPriceBySeatIdAndScheduleId(scheduleId, seatId);
            }
        }

        return TotalPriceCalculationResponse.builder()
                .seatNumbers(seatNumbers)
                .unitPrice(unitPrice)
                .quantity(seatIds.size())
                .totalPrice(totalPrice)
                .build();
    }

    public Seat findById(long id) {
        return seatRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat with id " + id + " not found"));
    }
}
