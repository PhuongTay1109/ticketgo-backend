package com.ticketgo.service.impl;

import com.ticketgo.dto.response.RouteSearchResponse;
import com.ticketgo.dto.response.ApiPaginationResponse;
import com.ticketgo.mapper.ScheduleMapper;
import com.ticketgo.model.Schedule;

import com.ticketgo.repository.specification.ScheduleSpecification;
import com.ticketgo.service.RouteService;
import com.ticketgo.service.ScheduleService;
import com.ticketgo.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteServiceImpl implements RouteService {
    private final ScheduleService scheduleService;
    private final SeatService seatService;

    @Override
    @Transactional
    public ApiPaginationResponse searchRoutes(String departureLocation,
                                              String arrivalLocation,
                                              LocalDate departureDate,
                                              String sortBy,
                                              String sortDirection,
                                              int pageNumber,
                                              int pageSize) {
        Specification<Schedule> spec = Specification
                .where(ScheduleSpecification.hasDepartureLocation(departureLocation))
                .and(ScheduleSpecification.hasArrivalLocation(arrivalLocation))
                .and(ScheduleSpecification.hasDepartureDate(departureDate))
                .and(ScheduleSpecification.withSorting(sortBy, sortDirection));

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Schedule> schedules = scheduleService.findAll(spec, pageable);

        List<RouteSearchResponse> scheduleDTOs = schedules.stream()
                .map(schedule -> {
                    long scheduleId = schedule.getScheduleId();

                    return ScheduleMapper.INSTANCE.toRouteSearchResponse(
                            schedule,
                            seatService.countAvailableSeatsBySchedule(scheduleId),
                            schedule.getPrice()
                    );
                })
                .collect(Collectors.toList());

        ApiPaginationResponse.Pagination pagination = new ApiPaginationResponse.Pagination(
                schedules.getNumber() + 1,
                schedules.getSize(),
                schedules.getTotalPages(),
                schedules.getTotalElements()
        );

        return new ApiPaginationResponse(HttpStatus.OK, "Search results", scheduleDTOs, pagination);
    }
}
