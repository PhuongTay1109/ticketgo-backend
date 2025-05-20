package com.ticketgo.dto;

import com.ticketgo.entity.Schedule;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleDTO {
    private Long scheduleId;
    private String routeName;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    public ScheduleDTO(Schedule schedule) {
        this.scheduleId = schedule.getScheduleId();
        this.routeName = schedule.getRoute().getRouteName();
        this.departureTime = schedule.getDepartureTime();
        this.arrivalTime = schedule.getArrivalTime();
    }
}
