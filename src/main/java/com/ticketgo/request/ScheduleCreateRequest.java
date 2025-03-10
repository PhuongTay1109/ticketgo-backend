package com.ticketgo.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class ScheduleCreateRequest {
    // Step 1: Select route
    private Long routeId;

    // Step 2: Select bus
    private Long busId;

    // Step 3: Select driver
    private Long driverId;

    // Step 4: Select departure time and arrival time
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    // Step 5: Select price
    private Double price;

    // Step 6: Select route stops
    private List<StopRequest> pickupStops;
    private List<StopRequest> dropoffStops;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StopRequest {
        private String location;
        private int stopOrder;
        private LocalDateTime arrivalTime;
    }
}
