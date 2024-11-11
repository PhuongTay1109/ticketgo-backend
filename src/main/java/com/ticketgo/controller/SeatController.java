package com.ticketgo.controller;

import com.ticketgo.dto.SeatDTO;
import com.ticketgo.dto.request.SeatReservationRequest;
import com.ticketgo.dto.request.TotalPriceCalculationRequest;
import com.ticketgo.dto.response.ApiResponse;
import com.ticketgo.dto.response.TotalPriceCalculationResponse;
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
        return new ApiResponse(HttpStatus.OK, "Lấy trạng thái các ghế thành công", resp);
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveSeats(@RequestBody SeatReservationRequest request) {
        seatService.reserveSeats(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("release")
    public ApiResponse releaseSeats(@RequestBody SeatReservationRequest request) {
        seatService.releaseReservedSeatsByCustomer();
        return new ApiResponse(HttpStatus.OK, "Hủy các ghế đã đặt thành công", null);
    }

    @PostMapping("/prices")
    public ApiResponse getSeatPrice(@RequestBody TotalPriceCalculationRequest request) {
        TotalPriceCalculationResponse resp = seatService.getSeatPrice(request);
        return new ApiResponse(HttpStatus.OK, "Lấy thông tin giá thành công", resp);
    }
}
