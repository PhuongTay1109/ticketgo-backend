package com.ticketgo.service.impl;

import com.ticketgo.model.Schedule;
import com.ticketgo.repository.ScheduleRepository;
import com.ticketgo.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepo;

    @Override
    public Schedule findById(long scheduleId) {
        return scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException(
                                "Schedule with id " + scheduleId + " not found"));
    }

    @Override
    public Page<Schedule> findAll(Specification<Schedule> spec, Pageable pageable) {
        return scheduleRepo.findAll(spec, pageable);
    }
}
