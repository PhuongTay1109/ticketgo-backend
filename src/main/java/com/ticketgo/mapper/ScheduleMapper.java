package com.ticketgo.mapper;

import com.ticketgo.dto.ScheduleDTO;
import com.ticketgo.model.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.Duration;

@Mapper
public interface ScheduleMapper {
    ScheduleMapper INSTANCE = Mappers.getMapper(ScheduleMapper.class);

    @Mapping(target = "routeName", source = "schedule.route.routeName")
    @Mapping(target = "busImage", source = "schedule.bus.busImage")
    @Mapping(target = "busType", source = "schedule.bus.busType")
    @Mapping(target = "busId", source = "schedule.bus.busId")
    @Mapping(target = "departureLocation", source = "schedule.route.departureLocation")
    @Mapping(target = "arrivalLocation", source = "schedule.route.arrivalLocation")
    @Mapping(target = "departureTime", source = "schedule.departureTime")
    @Mapping(target = "arrivalTime", source = "schedule.arrivalTime")
    @Mapping(target = "scheduleId", source = "schedule.scheduleId")
    @Mapping(target = "availableSeats", source = "availableSeats")
    @Mapping(target = "travelDuration", expression = "java(calculateTravelDuration(schedule))")
    ScheduleDTO toScheduleDTO(Schedule schedule, int availableSeats);

    default String calculateTravelDuration(Schedule schedule) {
        Duration duration = Duration.between(schedule.getDepartureTime(), schedule.getArrivalTime());
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%d giờ %d phút", hours, minutes);
    }
}

