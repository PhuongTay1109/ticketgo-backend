package com.ticketgo.service;

import com.ticketgo.dto.DriverDTO;
import com.ticketgo.request.DriverCreateRequest;
import com.ticketgo.request.DriverListRequest;
import com.ticketgo.request.DriverUpdateRequest;
import com.ticketgo.response.ApiPaginationResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface DriverService {
    ApiPaginationResponse list(DriverListRequest req);
    void add(DriverCreateRequest req);
    DriverDTO get(Long id);
    void update(Long id, DriverUpdateRequest req);
    void delete(Long id);

    DriverDTO getDriverForSchedule(Long scheduleId);

    List<DriverDTO> getAvailableDrivers(LocalDateTime departureTime, LocalDateTime arrivalTime);
}
