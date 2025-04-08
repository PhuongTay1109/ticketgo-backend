package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.constant.RedisKeys;
import com.ticketgo.request.PaymentRequest;
import com.ticketgo.service.BookingService;
import com.ticketgo.service.EmailService;
import com.ticketgo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(ApiVersion.V1 + "/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final EmailService emailService;
    private final RedissonClient redisson;

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
        String[] parts = orderInfo.split("-");

        Long bookingId = Long.parseLong(parts[0]);
        Long customerId = Long.parseLong(parts[1]);
        Long scheduleId = Long.parseLong(parts[2]);

        if (responseCode.equals("00")) {
            String bookingInfoKey = RedisKeys.userBookingInfoKey(customerId, scheduleId);
            log.info("Booking info key: {}", bookingInfoKey);
            redisson.getBucket(bookingInfoKey).delete();

            String vnPayUrlKey = RedisKeys.vnPayUrlKey(customerId, scheduleId);
            log.info("VNPay URL key: {}", vnPayUrlKey);
            redisson.getBucket(vnPayUrlKey).delete();

            String contactInfoKey = RedisKeys.contactInfoKey(customerId, scheduleId);
            log.info("Contact info key: {}", contactInfoKey);
            redisson.getBucket(contactInfoKey).delete();

            bookingService.setConfirmedVNPayBooking(bookingId);
            emailService.sendBookingInfo(bookingId);
            return ResponseEntity.status(302)
                    .header("Location",
                            frontendUrl + "/thankyou")
                    .build();
        } else {
            bookingService.setFailedVNPayBooking(bookingId);
            return ResponseEntity.status(302)
                    .header("Location",
                            "https://www.notion.so/Thanh-to-n-th-t-b-i-13d5b3bc1317803cb85ce85a1731485b")
                    .build();
        }
    }
}
