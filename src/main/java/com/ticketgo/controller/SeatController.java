package com.ticketgo.controller;

import com.ticketgo.dto.SeatStatusDTO;
import com.ticketgo.dto.response.ApiResponse;
import com.ticketgo.repository.SeatRepository;
import com.ticketgo.service.SeatService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;
    private final SeatRepository seatRepository;

    @GetMapping("")
    public ApiResponse getSeatStatus(@RequestParam Long scheduleId) {
        List<SeatStatusDTO> resp = seatService.getSeatStatusForSchedule(scheduleId);
        return new ApiResponse(HttpStatus.OK, "Get seats status", resp);
    }

}
