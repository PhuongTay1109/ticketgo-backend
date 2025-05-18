package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.request.CancelBookingRequest;
import com.ticketgo.request.PriceEstimationRequest;
import com.ticketgo.request.SaveBookingInfoRequest;
import com.ticketgo.request.SaveContactInfoRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.BookingService;
import com.ticketgo.service.SeatService;
import com.ticketgo.service.TicketService;
import com.ticketgo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping("/saveContactInfo")
    public ApiResponse saveCustomerContactInfo(@Valid @RequestBody SaveContactInfoRequest request) {
        bookingService.saveCustomerContactInfo(request);
        return new ApiResponse(
                HttpStatus.OK,
                "Lưu thông tin liên hệ thành công",
                null
        );
    }

    @GetMapping("/getSavedContactInfo")
    public ApiResponse saveCustomerContactInfo(@RequestParam long scheduleId) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thông tin liên hệ thành công",
                bookingService.getCustomerContactInfo(scheduleId)
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

    @GetMapping("/all-history")
    @PreAuthorize("hasRole('BUS_COMPANY')")
    public ApiPaginationResponse getAllBookingHistory(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) String contactName,
            @RequestParam(required = false) String contactEmail,
            @RequestParam(required = false) String routeName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String refundStatus,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {
        return bookingService.getAllBookingHistory(pageNumber, pageSize, bookingId, contactName, contactEmail, routeName, status, refundStatus, fromDate, toDate);
    }



    @GetMapping("/in-progress")
    public ResponseEntity<Boolean> checkInProgressTransaction() {
        return ResponseEntity
                .ok()
                .body(ticketService.existsReservedSeatsByCustomer());
    }

    @GetMapping("/step")
    public ApiResponse getBookingStep(@RequestParam Long scheduleId) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thông tin bước đặt vé thành công",
                bookingService.getBookingStep(scheduleId)
        );
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

    @PostMapping("/cancel")
    public ApiResponse cancelBooking(@RequestBody CancelBookingRequest req) {
        bookingService.cancelBooking(req);
        return new ApiResponse(
                HttpStatus.OK,
                "Hủy đặt vé thành công",
                null
        );
    }
}
