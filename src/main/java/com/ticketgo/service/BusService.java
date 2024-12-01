package com.ticketgo.service;

import com.ticketgo.dto.response.ApiPaginationResponse;
import com.ticketgo.model.Bus;

public interface BusService {
    Bus findBySchedule(long scheduleId);

    ApiPaginationResponse getAllBuses(int pageNumber, int pageSize) ;
}
