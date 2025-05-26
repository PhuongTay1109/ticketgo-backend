package com.ticketgo.controller;

import com.ticketgo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final EmailService emailService;

    @GetMapping("/api/v1/test")
    public ResponseEntity<Void> test() {
        emailService.sendBookingInfoReturn(69L, 26L, 70L, 32L);
        return ResponseEntity.ok(null);
    }
}
