package com.ticketgo.controller;

import com.ticketgo.dto.SeatDTO;
import com.ticketgo.dto.request.SeatReservationRequest;
import com.ticketgo.dto.response.ApiResponse;
import com.ticketgo.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ApiResponse reserveSeats(@RequestBody SeatReservationRequest request) {
        seatService.reserveSeats(request);

        return new ApiResponse(HttpStatus.OK, "Đặt giữ vé thành công cho khách hàng", null);
    }

    @PostMapping("/cancel-reserve")
    public ApiResponse cancelReserveSeats() {
        seatService.cancelReservedSeatsByCustomer();
        return new ApiResponse(HttpStatus.OK, "Hủy các vé đang giữ cho khách hàng thành công", null);
    }
}
