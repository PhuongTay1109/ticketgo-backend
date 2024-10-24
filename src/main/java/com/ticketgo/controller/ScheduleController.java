package com.ticketgo.controller;

import com.ticketgo.dto.ScheduleDTO;
import com.ticketgo.model.Schedule;
import com.ticketgo.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routes")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/search")
    public ResponseEntity<List<ScheduleDTO>> searchRoutes(
            @RequestParam String departureLocation,
            @RequestParam String arrivalLocation) {

        List<ScheduleDTO> schedules = scheduleService.searchRoutes(departureLocation, arrivalLocation);
        return ResponseEntity.ok(schedules);
    }
}
