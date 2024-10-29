package com.ticketgo.service;

import com.ticketgo.dto.response.ApiPaginationResponse;
import com.ticketgo.dto.response.RouteStopResponse;

import java.time.LocalDate;


public interface ScheduleService {
    ApiPaginationResponse searchRoutes(String departureLocation,
                                       String arrivalLocation,
                                       LocalDate departureDate,
                                       String sortBy,
                                       String sortDirection,
                                       int pageNumber,
                                       int pageSize);

    RouteStopResponse getRouteStops(long scheduleId);
}
