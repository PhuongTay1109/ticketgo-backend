package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.request.PriceEstimationRequest;
import com.ticketgo.request.SaveBookingInfoRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.BookingService;
import com.ticketgo.service.SeatService;
import com.ticketgo.service.TicketService;
import com.ticketgo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersion.V1 + "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final UserService userService;
    private final SeatService seatService;
    private final BookingService bookingService;
    private final TicketService ticketService;

    @GetMapping("/contact-info")
    public ApiResponse getCustomerContactInfo() {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thông tin liên hệ thành công",
                userService.getCustomerContactIno()
        );
    }

    @PostMapping("/estimated-prices")
    public ApiResponse getSeatPrice(@RequestBody PriceEstimationRequest request) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thông tin tạm tính thành công",
                seatService.getSeatPrice(request)
        );
    }

    @GetMapping("/trip-info")
    public ApiResponse getTripInfo(
            @RequestParam Long pickupStopId,
            @RequestParam Long dropOffStopId,
            @RequestParam Long scheduleId) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thông tin chuyến đi thành công",
                bookingService.getTripInformation(pickupStopId, dropOffStopId, scheduleId)
        );
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


    @PostMapping("/info")
    public ApiResponse saveBookingInfo(@RequestBody SaveBookingInfoRequest request) {
        bookingService.saveBookingInfo(request);
        return new ApiResponse(
                HttpStatus.CREATED,
                "Lưu thông tin xác nhận đặt vé thành công",
                null
        );
    }

    @GetMapping("/info")
    public ApiResponse getBookingInfo(@RequestParam Long scheduleId) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thông tin xác nhận đặt vé thành công",
                bookingService.getBookingInfo(scheduleId)
        );
    }
}
