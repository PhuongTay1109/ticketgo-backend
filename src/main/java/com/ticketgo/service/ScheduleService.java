package com.ticketgo.service;

import com.ticketgo.entity.Schedule;
import com.ticketgo.request.ScheduleCreateRequest;
import com.ticketgo.response.BusScheduleResponse;
import com.ticketgo.response.DriverScheduleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.YearMonth;

public interface ScheduleService {
    Schedule findById(long scheduleId);

    Page<Schedule> findAll(Specification<Schedule> spec, Pageable pageable);

    void create(ScheduleCreateRequest req);

    void updateScheduleStatus(Long scheduleId, String status);

    BusScheduleResponse getBusScheduleForMonth(Long busId, YearMonth month);
    DriverScheduleResponse getDriverScheduleForMonth(Long driverId, YearMonth month);

    void updateDriverForSchedule(Long scheduleId, Long driverId);
}
