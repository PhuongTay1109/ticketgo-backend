package com.ticketgo.mapper;

import com.ticketgo.dto.ScheduleDTO;
import com.ticketgo.model.Schedule;

import java.time.Duration;

public class ScheduleMapper {
    public static ScheduleDTO convertToDTO(Schedule schedule, int availableSeats) {
        Duration duration = Duration.between(schedule.getDepartureTime(), schedule.getArrivalTime());
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        String travelDuration = String.format("%d giờ %d phút", hours, minutes);

        return ScheduleDTO.builder()
                .routeName(schedule.getRoute().getRouteName())
                .busImage(schedule.getBus().getBusImage())
                .busType(schedule.getBus().getBusType())
                .departureLocation(schedule.getRoute().getDepartureLocation())
                .arrivalLocation(schedule.getRoute().getArrivalLocation())
                .departureTime(schedule.getDepartureTime())
                .arrivalTime(schedule.getArrivalTime())
                .availableSeats(availableSeats)
                .travelDuration(travelDuration)
                .build();
    }

}
