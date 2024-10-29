package com.ticketgo.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RouteStopDTO {
    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;
}
