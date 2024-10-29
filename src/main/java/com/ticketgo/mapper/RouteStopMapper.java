package com.ticketgo.mapper;

import com.ticketgo.dto.RouteStopDTO;
import com.ticketgo.model.RouteStop;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RouteStopMapper {
    RouteStopMapper INSTANCE = Mappers.getMapper(RouteStopMapper.class);

    RouteStopDTO toRouteStopDTO(RouteStop routeStop);
}
