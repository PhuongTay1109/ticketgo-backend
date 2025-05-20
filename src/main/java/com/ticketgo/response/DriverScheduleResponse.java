package com.ticketgo.response;

import com.ticketgo.dto.ScheduleDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class DriverScheduleResponse {
    private Long driverId;
    private String month;
    private Map<LocalDate, List<ScheduleDTO>> schedulesByDay;

    public DriverScheduleResponse(Long driverId, String month, Map<LocalDate, List<ScheduleDTO>> schedulesByDay) {
        this.driverId = driverId;
        this.month = month;
        this.schedulesByDay = schedulesByDay;
    }

    // Getters and setters
}

