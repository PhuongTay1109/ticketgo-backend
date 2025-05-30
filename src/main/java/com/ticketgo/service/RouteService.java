package com.ticketgo.service;

import com.ticketgo.dto.RouteDTO;
import com.ticketgo.request.AddRouteRequest;
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

    List<RouteDTO> getRoutes();

    void createRoute(AddRouteRequest request);

    void updateRoute(AddRouteRequest request);
}
