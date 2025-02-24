package com.ticketgo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripInformationResponse {
    private LocalDateTime departureTime;
    private String licensePlate;
    private String busType;
    private LocalDateTime pickupTime;
    private String pickupLocation;
    private LocalDateTime dropoffTime;
    private String dropoffLocation;
}
