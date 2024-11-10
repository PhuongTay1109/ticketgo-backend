package com.ticketgo.controller;

import com.ticketgo.dto.SeatDTO;
import com.ticketgo.dto.request.SeatReservationRequest;
import com.ticketgo.dto.response.ApiResponse;
import com.ticketgo.service.SeatService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;


    @GetMapping("")
    public ApiResponse getSeatStatus(@RequestParam Long scheduleId) {
        Map<String, List<List<SeatDTO>>> resp = seatService.getSeatStatusForSchedule(scheduleId);
        return new ApiResponse(HttpStatus.OK, "Get seats status", resp);
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveSeats(@RequestBody SeatReservationRequest request) {
        seatService.reserveSeats(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("release")
    public ResponseEntity<Void> releaseSeats(@RequestBody SeatReservationRequest request) {
        seatService.releaseReservedSeatsByCustomer();
        return ResponseEntity.ok().build();
    }
}
