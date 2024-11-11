package com.ticketgo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketgo.dto.request.BookingRequest;
import com.ticketgo.dto.response.ApiResponse;
import com.ticketgo.exception.AppException;
import com.ticketgo.service.BookingService;
import com.ticketgo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final BookingService bookingService;

    @PostMapping("/api/v1/vnpay")
    public ApiResponse createVNPayment(@RequestBody BookingRequest request) throws JsonProcessingException {
        paymentService.createVNPayment(request);
        return new ApiResponse(HttpStatus.OK, null, null);
    }

    @GetMapping("/api/v1/vnpay/return")
    @Transactional
    public ResponseEntity<Void> getVNPaymentReturn(@RequestParam("vnp_ResponseCode") String responseCode,
                                                   @RequestParam("vnp_OrderInfo") String orderInfo) throws JsonProcessingException {

        if (responseCode.equals("00")) {
            byte[] decodedBytes = Base64.getDecoder().decode(orderInfo);
            String decodedOrderInfo = new String(decodedBytes, StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            BookingRequest request = objectMapper.readValue(decodedOrderInfo, BookingRequest.class);
            log.info(request.toString());

            bookingService.saveBookingForVNPay(request);

            return ResponseEntity.status(302)
                    .header("Location", "https://www.notion.so/Daily-To-Do-Lists-f480e2ee83834a049efe7373ca769f22")
                    .build();
        } else {
            // Redirect tới trang lỗi nếu thanh toán thất bại
            return ResponseEntity.status(302)  // 302 là mã HTTP cho chuyển hướng
                    .header("Location", "http://yourfrontend.com/thank-you?responseCode=01&orderInfo=" + orderInfo)
                    .build();
        }
    }
}
