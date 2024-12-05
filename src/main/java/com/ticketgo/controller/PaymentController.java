package com.ticketgo.controller;

import com.ticketgo.dto.request.PaymentRequest;
import com.ticketgo.service.BookingService;
import com.ticketgo.service.EmailService;
import com.ticketgo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final EmailService emailService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @PostMapping("vnpay")
    public String createVNPayment(@RequestBody PaymentRequest request) {
        return paymentService.createVNPayment(request);
    }

    @GetMapping("vnpay/return")
    @Transactional
    public ResponseEntity<Void> getVNPaymentReturn(@RequestParam("vnp_ResponseCode") String responseCode,
                                                   @RequestParam("vnp_OrderInfo") String orderInfo) {

        if (responseCode.equals("00")) {
            bookingService.setConfirmedVNPayBooking(Long.parseLong(orderInfo));
            emailService.sendBookingInfo(Long.parseLong(orderInfo));
            return ResponseEntity.status(302)
                    .header("Location",
                            frontendUrl + "/thankyou")
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
