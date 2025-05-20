package com.ticketgo.response;

import com.ticketgo.dto.ScheduleDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class BusScheduleResponse {
    private Long busId;
    private String month;
    private Map<LocalDate, List<ScheduleDTO>> schedulesByDay;

    public BusScheduleResponse(Long busId, String month, Map<LocalDate, List<ScheduleDTO>> schedulesByDay) {
        this.busId = busId;
        this.month = month;
        this.schedulesByDay = schedulesByDay;
    }
}
