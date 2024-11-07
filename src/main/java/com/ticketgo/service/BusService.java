package com.ticketgo.service;

import com.ticketgo.model.Bus;

public interface BusService {
    Bus findBySchedule(long scheduleId);
}
