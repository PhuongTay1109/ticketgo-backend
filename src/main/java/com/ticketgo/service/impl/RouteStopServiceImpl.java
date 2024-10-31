package com.ticketgo.service.impl;

import com.ticketgo.dto.RouteStopDTO;
import com.ticketgo.dto.response.RouteStopResponse;
import com.ticketgo.exception.AppException;
import com.ticketgo.mapper.RouteStopMapper;
import com.ticketgo.model.RouteStop;
import com.ticketgo.model.Schedule;
import com.ticketgo.model.StopType;
import com.ticketgo.repository.ScheduleRepository;
import com.ticketgo.service.RouteStopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RouteStopServiceImpl implements RouteStopService {
    private final ScheduleRepository scheduleRepository;

    @Override
    public RouteStopResponse getRouteStops(long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new AppException(
                        "There is no schedule",
                        HttpStatus.NOT_FOUND
                ));

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
}
