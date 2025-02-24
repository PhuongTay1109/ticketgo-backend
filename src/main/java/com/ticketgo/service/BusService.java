package com.ticketgo.service;

import com.ticketgo.request.BusListRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.entity.Bus;

public interface BusService {
    Bus findBySchedule(long scheduleId);

    ApiPaginationResponse getAllBuses(BusListRequest req) ;
}
