package com.ticketgo.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripInformationResponse {
    private LocalDateTime departureTime;
    private String licensePlate;
    private String busType;
    private LocalDateTime pickupTime;
    private String pickupLocation;
    private LocalDateTime dropoffTime;
    private String dropoffLocation;
}
