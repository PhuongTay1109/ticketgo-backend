package com.ticketgo.dto.request;

import com.ticketgo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/pay")
    public ResponseEntity<Void> pay() {
        bookingService.makeBooking();
        return ResponseEntity.ok().build();
    }
}
