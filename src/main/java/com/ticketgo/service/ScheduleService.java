package com.ticketgo.service;

import com.ticketgo.entity.Schedule;
import com.ticketgo.request.ScheduleCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ScheduleService {
    Schedule findById(long scheduleId);

    Page<Schedule> findAll(Specification<Schedule> spec, Pageable pageable);

    void create(ScheduleCreateRequest req);
}
