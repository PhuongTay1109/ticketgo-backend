package com.ticketgo.service.impl;

import com.ticketgo.dto.RouteDTO;
import com.ticketgo.entity.Route;
import com.ticketgo.entity.Schedule;
import com.ticketgo.mapper.RouteMapper;
import com.ticketgo.mapper.ScheduleMapper;
import com.ticketgo.repository.RouteRepository;
import com.ticketgo.repository.specification.ScheduleSpecification;
import com.ticketgo.request.AddRouteRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.response.PopularRoutesResponse;
import com.ticketgo.response.RouteSearchResponse;
import com.ticketgo.service.RouteService;
import com.ticketgo.service.ScheduleService;
import com.ticketgo.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final RouteRepository routeRepo;

    @Override
    @Transactional
    public ApiPaginationResponse searchRoutes(String departureLocation,
                                              String arrivalLocation,
                                              LocalDate departureDate,
                                              String sortBy,
                                              String sortDirection,
                                              int pageNumber,
                                              int pageSize) {
        log.info("[RouteService] searchRoutes called with: departureLocation={}, arrivalLocation={}, departureDate={}, sortBy={}, sortDirection={}, pageNumber={}, pageSize={}",
                departureLocation, arrivalLocation, departureDate, sortBy, sortDirection, pageNumber, pageSize);


        Specification<Schedule> spec = Specification.where(null);

        if (departureLocation != null) {
            spec = spec.and(ScheduleSpecification.hasDepartureLocation(departureLocation));
        }

        if (arrivalLocation != null) {
            spec = spec.and(ScheduleSpecification.hasArrivalLocation(arrivalLocation));
        }

        if (departureDate != null) {
            spec = spec.and(ScheduleSpecification.hasDepartureDate(departureDate));
        }
        spec = spec.and(ScheduleSpecification.hasVisibility());

        String sortField = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy;
        Sort.Direction sortDir = (sortDirection == null || sortDirection.isBlank())
                ? Sort.Direction.DESC
                : Sort.Direction.fromString(sortDirection);

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(sortDir, sortField));

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

        ApiPaginationResponse response = new ApiPaginationResponse(HttpStatus.OK, "Kết quả tìm kiếm", scheduleDTOs, pagination);

        log.info("[RouteService] searchRoutes returning: totalElements={}, totalPages={}, currentPage={}, pageSize={}, dataCount={}",
                schedules.getTotalElements(),
                pagination.getTotalPages(),
                pagination.getPageNumber(),
                pagination.getPageSize(),
                scheduleDTOs != null ? scheduleDTOs.size() : 0);

        return response;

    }

    @Override
    public List<PopularRoutesResponse> getPopularRoutes() {
        List<Route> routes = routeRepo.findAllByDepartureLocationContains("Sài Gòn");
        return routes.stream()
                .map(route -> new PopularRoutesResponse(
                                route.getRouteImage(),
                                route.getRouteName(),
                          200000L))
                .toList();
    }

    @Override
    public List<RouteDTO> getRoutes() {
        return routeRepo.findAll().stream()
                .map(RouteMapper.INSTANCE::fromEntityToDTO)
                .toList();
    }

    @Override
    public void createRoute(AddRouteRequest request) {
        Route route = Route.builder()
                .routeName(request.getRouteName())
                .departureLocation(request.getDepartureLocation())
                .arrivalLocation(request.getArrivalLocation())
                .departureAddress("")
                .arrivalAddress("")
                .routeImage(request.getRouteImage())
                .build();

        routeRepo.save(route);
    }

    @Override
    public void updateRoute(AddRouteRequest request) {
        Route route = routeRepo.findById(request.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found"));

        route.setRouteImage(request.getRouteImage());

        routeRepo.save(route);
    }

}
