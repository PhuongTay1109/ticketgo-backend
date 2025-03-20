package com.ticketgo.mapper;

import com.ticketgo.entity.Route;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RouteMapper {
    RouteMapper INSTANCE = Mappers.getMapper(RouteMapper.class);

    com.ticketgo.dto.RouteDTO fromEntityToDTO(Route route);
}
