package com.ticketgo.controller;

import com.ticketgo.dto.request.SearchRoutesRequest;
import com.ticketgo.dto.response.ApiPaginationResponse;
import com.ticketgo.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routes")
@Slf4j
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping("/search")
    public ApiPaginationResponse searchRoutes(@Valid @RequestBody SearchRoutesRequest request) {
        return scheduleService.searchRoutes(
                request.getDepartureLocation(),
                request.getArrivalLocation(),
                request.getDepartureDate(),
                request.getSortBy(),
                request.getSortDirection(),
                request.getPageNumber(),
                request.getPageSize()
        );
    }
}
