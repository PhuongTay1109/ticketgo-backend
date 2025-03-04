package com.ticketgo.mapper;

import com.ticketgo.dto.DriverDTO;
import com.ticketgo.entity.Driver;
import com.ticketgo.request.DriverCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DriverMapper {
    DriverMapper INSTANCE = Mappers.getMapper(DriverMapper.class);

    Driver fromCreateRequestToEntity(DriverCreateRequest req);
    DriverDTO fromEntityToDTO(Driver driver);
}
