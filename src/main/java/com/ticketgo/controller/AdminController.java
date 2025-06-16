package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.dto.BookingRequestDTO;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersion.V1)
public class AdminController {

    private final TicketService ticketService;

    @PostMapping("/admin-reserve")
    public ApiResponse reserveSeats(@RequestBody List<String> ticketCodes) {
        ticketService.reserveSeats(ticketCodes);
        return new ApiResponse(
                HttpStatus.OK,
                "Đặt giữ vé thành công cho khách hàng",
                null
        );
    }

    @PostMapping("/admin-booking")
    public ApiResponse adminBooking(@RequestBody BookingRequestDTO dto) {
        ticketService.adminBooking(dto);
        return new ApiResponse(
                HttpStatus.OK,
                "Đặt vé thành công",
                null
        );
    }
}
