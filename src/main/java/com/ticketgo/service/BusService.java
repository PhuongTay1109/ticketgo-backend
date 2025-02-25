package com.ticketgo.service;

import com.ticketgo.dto.BusDTO;
import com.ticketgo.entity.Bus;
import com.ticketgo.request.BusListRequest;
import com.ticketgo.response.ApiPaginationResponse;

public interface BusService {
    Bus findBySchedule(long scheduleId);

    ApiPaginationResponse getBuses(BusListRequest req);
    void createBus(BusDTO dto);
    BusDTO getBusById(Long id);
    void updateBus(Long id, BusDTO dto);
    void deleteBus(Long id);
}
