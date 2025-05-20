package com.ticketgo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ticketgo.enums.ScheduleStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteSearchResponse {
    private long scheduleId;
    private String routeName;
    private String busImage;
    private String busType;
    private String busId;
    private LocalDateTime departureTime;
    private String departureLocation;
    private String departureAddress;
    private String arrivalAddress;
    private LocalDateTime arrivalTime;
    private String arrivalLocation;
    private double price;
    private int availableSeats;
    private String travelDuration;
    private String scheduleStatus;
}
