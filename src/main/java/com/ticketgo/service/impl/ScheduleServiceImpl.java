package com.ticketgo.service.impl;

import com.ticketgo.dto.ScheduleDTO;
import com.ticketgo.entity.*;
import com.ticketgo.enums.BookingStatus;
import com.ticketgo.enums.ScheduleStatus;
import com.ticketgo.enums.StopType;
import com.ticketgo.enums.TicketStatus;
import com.ticketgo.exception.AppException;
import com.ticketgo.repository.*;
import com.ticketgo.request.ScheduleCreateRequest;
import com.ticketgo.response.BusScheduleResponse;
import com.ticketgo.response.DriverScheduleResponse;
import com.ticketgo.service.EmailService;
import com.ticketgo.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepo;
    private final TicketRepository ticketRepo;
    private final BusRepository busRepo;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;
    private final DriverRepository driverRepository;

    @Override
    public Schedule findById(long scheduleId) {
        return scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException(
                                "Schedule with id " + scheduleId + " not found"));
    }

    @Override
    public Page<Schedule> findAll(Specification<Schedule> spec, Pageable pageable) {
        return scheduleRepo.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public void create(ScheduleCreateRequest req) {
        Set<RouteStop> stops = new HashSet<>();

        List<ScheduleCreateRequest.StopRequest> pickupStops = req.getPickupStops();
        List<ScheduleCreateRequest.StopRequest> dropoffStops = req.getDropoffStops();

        if (pickupStops != null) {
            for (ScheduleCreateRequest.StopRequest stop : pickupStops) {
                stops.add(RouteStop.builder()
                        .location(stop.getLocation())
                        .stopOrder(stop.getStopOrder())
                        .arrivalTime(stop.getArrivalTime())
                        .stopType(StopType.PICKUP)
                        .build());
            }
        }

        if (dropoffStops != null) {
            for (ScheduleCreateRequest.StopRequest stop : dropoffStops) {
                stops.add(RouteStop.builder()
                        .location(stop.getLocation())
                        .stopOrder(stop.getStopOrder())
                        .arrivalTime(stop.getArrivalTime())
                        .stopType(StopType.DROPOFF)
                        .build());
            }
        }

        Schedule schedule = Schedule.builder()
                .bus(Bus.builder()
                        .busId(req.getBusId())
                        .build())
                .route(Route.builder()
                        .routeId(req.getRouteId())
                        .build())
                .driver(Driver.builder()
                        .driverId(req.getDriverId())
                        .build())
                .departureTime(req.getDepartureTime())
                .arrivalTime(req.getArrivalTime())
                .price(req.getPrice())
                .isVisible(true)
                .status(ScheduleStatus.SCHEDULED)
                .build();

        stops.forEach(stop -> stop.setSchedule(schedule));
        schedule.setStops(stops);

        scheduleRepo.save(schedule);

        Set<Seat> seats = busRepo.findByBusId(req.getBusId())
                .orElseThrow(() -> new RuntimeException("Bus with id " + req.getBusId() + " not found"))
                .getSeats();

        for(Seat seat : seats) {
            Ticket ticket = Ticket.builder()
                    .seat(seat)
                    .schedule(schedule)
                    .status(TicketStatus.AVAILABLE)
                    .price(req.getPrice())
                    .build();
            ticketRepo.save(ticket);
        }

        log.info("Schedule created successfully with id: {}", schedule.getScheduleId());
    }

    @Override
    public void updateScheduleStatus(Long scheduleId, String status) {
        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new AppException("Schedule with id " + scheduleId + " not found", HttpStatus.NOT_FOUND));

        if (status.equalsIgnoreCase("Đang chạy")) {
            schedule.setStatus(ScheduleStatus.IN_PROGRESS);
            schedule.setIsVisible(true);
        } else if (status.equalsIgnoreCase("Hoàn thành")) {
            schedule.setStatus(ScheduleStatus.COMPLETED);
            schedule.setIsVisible(true);
            bookingRepository.updateStatusByScheduleId(scheduleId, BookingStatus.COMPLETED);
        } else {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        scheduleRepo.save(schedule);
        log.info("Schedule status updated successfully for id: {}", scheduleId);
    }

    @Override
    public BusScheduleResponse getBusScheduleForMonth(Long busId, YearMonth month) {
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.atEndOfMonth().atTime(LocalTime.MAX);

        List<Schedule> schedules = scheduleRepo.findSchedulesByBusAndMonth(busId, start, end);

        Map<LocalDate, List<ScheduleDTO>> groupedSchedules = schedules.stream()
                .map(ScheduleDTO::new)
                .collect(Collectors.groupingBy(s -> s.getDepartureTime().toLocalDate()));

        return new BusScheduleResponse(busId, month.toString(), groupedSchedules);
    }

    @Override
    public DriverScheduleResponse getDriverScheduleForMonth(Long driverId, YearMonth month) {
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.atEndOfMonth().atTime(LocalTime.MAX);

        List<Schedule> schedules = scheduleRepo.findSchedulesByDriverAndMonth(driverId, start, end);

        Map<LocalDate, List<ScheduleDTO>> groupedSchedules = schedules.stream()
                .map(ScheduleDTO::new)
                .collect(Collectors.groupingBy(s -> s.getDepartureTime().toLocalDate()));

        return new DriverScheduleResponse(driverId, month.toString(), groupedSchedules);
    }

    @Override
    @Transactional
    public void updateDriverForSchedule(Long scheduleId, Long driverId) {
        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new AppException("Schedule with id " + scheduleId + " not found", HttpStatus.NOT_FOUND));

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new AppException("Driver with id " + driverId + " not found", HttpStatus.NOT_FOUND));

        schedule.setDriver(driver);
        scheduleRepo.save(schedule);

        log.info("Driver updated successfully for schedule id: {}", scheduleId);

        List<Booking> bookings = bookingRepository.findAllByScheduleId(scheduleId);
        for (Booking booking : bookings) {
            emailService.sendUpdateDriver(schedule, booking.getBookingId(), driver);
        }
    }
}
