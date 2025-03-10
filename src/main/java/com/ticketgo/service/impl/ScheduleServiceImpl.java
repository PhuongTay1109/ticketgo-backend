package com.ticketgo.service.impl;

import com.ticketgo.entity.*;
import com.ticketgo.enums.StopType;
import com.ticketgo.repository.ScheduleRepository;
import com.ticketgo.request.ScheduleCreateRequest;
import com.ticketgo.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepo;

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
                .build();

        stops.forEach(stop -> stop.setSchedule(schedule));
        schedule.setStops(stops);

        scheduleRepo.save(schedule);
    }
}
