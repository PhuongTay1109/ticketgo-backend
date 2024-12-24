package com.ticketgo.controller;

import com.ticketgo.dto.CustomerContactInfoDTO;
import com.ticketgo.request.PriceEstimationRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.response.PriceEstimationResponse;
import com.ticketgo.response.TripInformationResponse;
import com.ticketgo.service.BookingService;
import com.ticketgo.service.SeatService;
import com.ticketgo.service.TicketService;
import com.ticketgo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final UserService userService;
    private final SeatService seatService;
    private final BookingService bookingService;
    private final TicketService ticketService;

    @GetMapping("/contact-info")
    public ApiResponse getCustomerContactInfo() {
        CustomerContactInfoDTO resp = userService.getCustomerContactIno();
        return new ApiResponse(HttpStatus.OK, "Lấy thông tin liên hệ thành công", resp);
    }

    @PostMapping("/estimated-prices")
    public ApiResponse getSeatPrice(@RequestBody PriceEstimationRequest request) {
        PriceEstimationResponse resp = seatService.getSeatPrice(request);
        return new ApiResponse(HttpStatus.OK, "Lấy thông tin tạm tính thành công", resp);
    }

    @GetMapping("/trip-info")
    public ApiResponse getTripInfo(
            @RequestParam Long pickupStopId,
            @RequestParam Long dropOffStopId,
            @RequestParam Long scheduleId) {
        TripInformationResponse resp = bookingService.getTripInformation(pickupStopId, dropOffStopId, scheduleId);
        return new ApiResponse(HttpStatus.OK, "Lấy thông tin chuyến đi thành công", resp);
    }

    @GetMapping("/history")
    public ApiPaginationResponse getBookingHistory(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        return bookingService.getBookingHistoryForCustomer(pageNumber, pageSize);
    }

    @GetMapping("/in-progress")
    public ResponseEntity<Boolean> checkInProgressTransaction() {
        return ResponseEntity
                .ok()
                .body(ticketService.existsReservedSeatsByCustomer());
    }
}
