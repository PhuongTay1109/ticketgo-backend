package com.ticketgo.service;

import com.ticketgo.dto.ScheduleDTO;
import com.ticketgo.dto.response.ApiPaginationResponse;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    ApiPaginationResponse searchRoutes(String departureLocation,
                                       String arrivalLocation,
                                       LocalDate departureDate,
                                       String sortBy,
                                       String sortDirection,
                                       int pageNumber,
                                       int pageSize);
}
