package com.ticketgo.service.impl;

import com.ticketgo.dto.RouteStopDTO;
import com.ticketgo.dto.response.RouteStopResponse;
import com.ticketgo.mapper.RouteStopMapper;
import com.ticketgo.model.RouteStop;
import com.ticketgo.model.Schedule;
import com.ticketgo.model.StopType;
import com.ticketgo.repository.RouteStopRepository;
import com.ticketgo.service.RouteStopService;
import com.ticketgo.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RouteStopServiceImpl implements RouteStopService {

    private final ScheduleService scheduleService;
    private final RouteStopRepository routeStopRepo;

    @Override
    public RouteStopResponse getRouteStops(long scheduleId) {
        Schedule schedule = scheduleService.findById(scheduleId);

        Set<RouteStop> allStops = schedule.getStops();

        List<RouteStopDTO> pickupStops = allStops.stream()
                .filter(stop -> StopType.PICKUP == stop.getStopType())
                .sorted(Comparator.comparingInt(RouteStop::getStopOrder))
                .map(RouteStopMapper.INSTANCE::toRouteStopDTO)
                .toList();

        List<RouteStopDTO> dropoffStops = allStops.stream()
                .filter(stop -> StopType.DROPOFF == stop.getStopType())
                .sorted(Comparator.comparingInt(RouteStop::getStopOrder))
                .map(RouteStopMapper.INSTANCE::toRouteStopDTO)
                .toList();

        return RouteStopResponse.builder()
                .pickup(pickupStops)
                .dropoff(dropoffStops)
                .build();
    }

    @Override
    public List<RouteStopDTO> getPickupStops(long scheduleId) {
        List<RouteStop> pickupStops = routeStopRepo.getPickupRouteStopsByScheduleId(scheduleId);
        return pickupStops.stream()
                .map(RouteStopMapper.INSTANCE::toRouteStopDTO)
                .toList();
    }

    @Override
    public List<RouteStopDTO> getDropoffStops(long scheduleId) {
        List<RouteStop> dropoffStops = routeStopRepo.getDropoffRouteStopsByScheduleId(scheduleId);
        return dropoffStops.stream()
                .map(RouteStopMapper.INSTANCE::toRouteStopDTO)
                .toList();
    }

    @Override
    public RouteStop findById(long id) {
        return routeStopRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Route stop with id " + id + " not found"));
    }
}
