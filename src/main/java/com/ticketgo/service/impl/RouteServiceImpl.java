package com.ticketgo.service.impl;

import com.ticketgo.dto.RouteStopDTO;
import com.ticketgo.dto.ScheduleDTO;
import com.ticketgo.dto.response.ApiPaginationResponse;
import com.ticketgo.dto.response.RouteStopResponse;
import com.ticketgo.exception.AppException;
import com.ticketgo.mapper.RouteStopMapper;
import com.ticketgo.mapper.ScheduleMapper;
import com.ticketgo.model.RouteStop;
import com.ticketgo.model.Schedule;
import com.ticketgo.model.StopType;

import com.ticketgo.repository.ScheduleRepository;
import com.ticketgo.repository.specification.ScheduleSpecification;
import com.ticketgo.service.RouteService;
import com.ticketgo.service.RouteStopService;
import com.ticketgo.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteServiceImpl implements RouteService {
    private final ScheduleRepository scheduleRepository;
    private final SeatService seatService;

    @Override
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

        log.info("Page number {}", pageNumber);

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Schedule> schedules = scheduleRepository.findAll(spec, pageable);

        List<ScheduleDTO> scheduleDTOs = schedules.stream()
                .map(schedule -> ScheduleMapper.INSTANCE.toScheduleDTO(
                        schedule,
                        schedule.getBus().getTotalSeats()
                                - seatService.getBookedSeatsCountForSchedule(schedule.getScheduleId())
                ))
                .collect(Collectors.toList());

        ApiPaginationResponse.Pagination pagination = new ApiPaginationResponse.Pagination(
                schedules.getNumber() + 1,
                schedules.getSize(),
                schedules.getTotalPages(),
                schedules.getTotalElements()
        );

        return new ApiPaginationResponse(HttpStatus.OK, "Kết quả tìm kiếm", scheduleDTOs, pagination);
    }
}
