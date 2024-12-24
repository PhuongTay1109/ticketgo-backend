package com.ticketgo.service;

import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.response.PopularRoutesResponse;

import java.time.LocalDate;
import java.util.List;


public interface RouteService {
    ApiPaginationResponse searchRoutes(String departureLocation,
                                       String arrivalLocation,
                                       LocalDate departureDate,
                                       String sortBy,
                                       String sortDirection,
                                       int pageNumber,
                                       int pageSize);

    List<PopularRoutesResponse> getPopularRoutes();
}
