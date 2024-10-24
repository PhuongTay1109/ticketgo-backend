package com.ticketgo.service.impl;

import com.ticketgo.dto.ScheduleDTO;
import com.ticketgo.mapper.ScheduleMapper;
import com.ticketgo.model.Schedule;
import com.ticketgo.repository.ScheduleRepository;
import com.ticketgo.repository.specification.ScheduleSpecification;
import com.ticketgo.service.BookingService;
import com.ticketgo.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final BookingService bookingService;

    private final ScheduleMapper scheduleMapper;

    @Override
    public List<ScheduleDTO> searchRoutes(String departureLocation, String arrivalLocation) {
        Specification<Schedule> spec = Specification
                .where(ScheduleSpecification.hasDepartureLocation(departureLocation))
                .and(ScheduleSpecification.hasArrivalLocation(arrivalLocation));

        List<Schedule> schedules = scheduleRepository.findAll(spec);

        return schedules.stream()
                .map(schedule -> scheduleMapper.INSTANCE.toScheduleDTO(
                        schedule,
                        schedule.getBus().getTotalSeats()
                                - bookingService.getBookedSeatsCountForSchedule(schedule.getScheduleId())
                ))
                .collect(Collectors.toList());
    }
}
