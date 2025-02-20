package com.ticketgo.service;

import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.entity.Bus;

public interface BusService {
    Bus findBySchedule(long scheduleId);

    ApiPaginationResponse getAllBuses(int pageNumber, int pageSize) ;
}
