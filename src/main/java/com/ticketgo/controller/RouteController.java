package com.ticketgo.controller;


import com.ticketgo.dto.request.RouteSearchRequest;
import com.ticketgo.dto.response.ApiPaginationResponse;
import com.ticketgo.dto.response.ApiResponse;
import com.ticketgo.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routes")
@Slf4j
public class RouteController {
    private final RouteService routeService;

    @PostMapping("/search")
    public ApiPaginationResponse searchRoutes(@Valid @RequestBody RouteSearchRequest request) {
        return  routeService.searchRoutes(
                request.getDepartureLocation(),
                request.getArrivalLocation(),
                request.getDepartureDate(),
                request.getSortBy(),
                request.getSortDirection(),
                request.getPageNumber(),
                request.getPageSize()
        );
    }

    @GetMapping("/popular")
    public ApiResponse searchRoutes() {
        return new ApiResponse(
                        HttpStatus.OK,
                        "Lấy danh sách các tuyến đường phổ biến",
                        routeService.getPopularRoutes());
    }
}

