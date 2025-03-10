package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.request.ScheduleCreateRequest;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersion.V1 + "/schedules")
@Slf4j
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping
    @PreAuthorize("hasRole('BUS_COMPANY')")
    public ApiResponse create(@RequestBody ScheduleCreateRequest req) {
        scheduleService.create(req);
        return new ApiResponse(
                HttpStatus.CREATED,
                "Tạo chuyến xe thành công",
                null
        );
    }
}
