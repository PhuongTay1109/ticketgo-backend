package com.ticketgo.controller;

import com.ticketgo.dto.request.PaymentRequest;
import com.ticketgo.dto.response.ApiResponse;
import com.ticketgo.service.BookingService;
import com.ticketgo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final BookingService bookingService;

    @PostMapping("/api/v1/vnpay")
    public ApiResponse createVNPayment(@RequestBody PaymentRequest request) {
        paymentService.createVNPayment(request);
        return new ApiResponse(HttpStatus.OK, null, null);
    }

    @GetMapping("/api/v1/vnpay/return")
    @Transactional
    public ResponseEntity<Void> getVNPaymentReturn(@RequestParam("vnp_ResponseCode") String responseCode,
                                                   @RequestParam("vnp_OrderInfo") String orderInfo) {

        if (responseCode.equals("00")) {
            bookingService.setConfirmedVNPayBooking(Long.parseLong(orderInfo));
            return ResponseEntity.status(302)
                    .header("Location",
                            "https://www.notion.so/Thanh-to-n-th-nh-c-ng-13d5b3bc131780a0b01bc48e43523669")
                    .build();
        } else {
            bookingService.setFailedVNPayBooking(Long.parseLong(orderInfo));
            return ResponseEntity.status(302)
                    .header("Location",
                            "https://www.notion.so/Thanh-to-n-th-t-b-i-13d5b3bc1317803cb85ce85a1731485b")
                    .build();
        }
    }
}
