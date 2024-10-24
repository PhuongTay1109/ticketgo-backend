package com.ticketgo.service;

import com.ticketgo.dto.ScheduleDTO;
import com.ticketgo.model.Schedule;

import java.util.List;

public interface ScheduleService {
    List<ScheduleDTO> searchRoutes(String departureLocation, String arrivalLocation);
}
