package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.dto.TicketRemainingTimeDTO;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersion.V1 + "/tickets")
public class TicketController {
    private final TicketService ticketService;

    @GetMapping("/{ticketCode}/remain-time")
    public ApiResponse getRemainingTime(@PathVariable String ticketCode) {
        Long remainingTime = ticketService.getTicketRemainingTime(ticketCode);
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thời gian còn lại thành công",
                new TicketRemainingTimeDTO(remainingTime)
        );
    }
}
