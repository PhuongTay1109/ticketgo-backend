package com.ticketgo.service;

import com.ticketgo.dto.response.ApiPaginationResponse;

import java.time.LocalDate;


public interface RouteService {
    ApiPaginationResponse searchRoutes(String departureLocation,
                                       String arrivalLocation,
                                       LocalDate departureDate,
                                       String sortBy,
                                       String sortDirection,
                                       int pageNumber,
                                       int pageSize);
}
