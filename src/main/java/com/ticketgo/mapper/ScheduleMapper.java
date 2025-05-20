package com.ticketgo.mapper;

import com.ticketgo.response.RouteSearchResponse;
import com.ticketgo.entity.Schedule;
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
    @Mapping(target = "departureAddress", source = "schedule.route.departureAddress")
    @Mapping(target = "arrivalAddress", source = "schedule.route.arrivalAddress")
    @Mapping(target = "departureTime", source = "schedule.departureTime")
    @Mapping(target = "arrivalTime", source = "schedule.arrivalTime")
    @Mapping(target = "scheduleId", source = "schedule.scheduleId")
    @Mapping(target = "availableSeats", source = "availableSeats")
    @Mapping(target = "travelDuration", expression = "java(calculateTravelDuration(schedule))")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "scheduleStatus", expression = "java(schedule.getStatus().getDisplayName())")
    RouteSearchResponse toRouteSearchResponse(Schedule schedule, int availableSeats, double price);

    default String calculateTravelDuration(Schedule schedule) {
        Duration duration = Duration.between(schedule.getDepartureTime(), schedule.getArrivalTime());
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%d giờ %d phút", hours, minutes);
    }
}

