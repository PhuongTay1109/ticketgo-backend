package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.request.SeatReservationRequest;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersion.V1 + "/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @GetMapping("")
    public ApiResponse getSeatStatus(@RequestParam Long scheduleId) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy trạng thái các ghế thành công",
                seatService.getSeatStatusForSchedule(scheduleId)
        );
    }

    @PostMapping("/reserve")
    public ApiResponse reserveSeats(@RequestBody SeatReservationRequest request) {
        seatService.reserveSeats(request);
        return new ApiResponse(
                HttpStatus.OK,
                "Đặt giữ vé thành công cho khách hàng",
                null
        );
    }

    @PostMapping("/cancel-reserve")
    public ApiResponse cancelReserveSeats(@RequestParam Long scheduleId) {
        seatService.cancelReservedSeatsByCustomer(scheduleId);
        return new ApiResponse(
                HttpStatus.OK,
                "Hủy các vé đang giữ cho khách hàng thành công",
                null
        );
    }
}
