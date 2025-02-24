package com.ticketgo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteStopDTO {
    private Long stopId;
    private String location;
    private LocalDateTime arrivalTime;
}
