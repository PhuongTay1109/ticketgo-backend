package com.ticketgo.mapper;

import com.ticketgo.dto.BusDTO;
import com.ticketgo.model.Bus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.format.DateTimeFormatter;

@Mapper
public interface BusMapper {

    BusMapper INSTANCE = Mappers.getMapper(BusMapper.class);

    @Mapping(target = "registrationExpiry", expression = "java(formatDate(bus.getRegistrationExpiry()))")
    @Mapping(target = "expirationDate", expression = "java(formatDate(bus.getExpirationDate()))")
    BusDTO toBusDTO(Bus bus);

    default String formatDate(java.time.LocalDate date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
}
