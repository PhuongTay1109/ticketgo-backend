package com.ticketgo.service;

import com.ticketgo.dto.DriverDTO;
import com.ticketgo.request.DriverCreateRequest;
import com.ticketgo.request.DriverListRequest;
import com.ticketgo.request.DriverUpdateRequest;
import com.ticketgo.response.ApiPaginationResponse;

public interface DriverService {
    ApiPaginationResponse list(DriverListRequest req);
    void add(DriverCreateRequest req);
    DriverDTO get(Long id);
    void update(Long id, DriverUpdateRequest req);
    void delete(Long id);

    DriverDTO getDriverForSchedule(Long scheduleId);
}
