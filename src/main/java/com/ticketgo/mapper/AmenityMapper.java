package com.ticketgo.mapper;

import com.ticketgo.dto.AmenityDTO;
import com.ticketgo.model.Amenity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AmenityMapper {
    AmenityMapper INSTANCE = Mappers.getMapper(AmenityMapper.class);

    AmenityDTO toAmenityDTO(Amenity amenity);
}
