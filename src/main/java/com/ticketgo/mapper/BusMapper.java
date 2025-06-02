package com.ticketgo.mapper;

import com.ticketgo.dto.BusDTO;
import com.ticketgo.entity.Bus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper
public interface BusMapper {

    BusMapper INSTANCE = Mappers.getMapper(BusMapper.class);

    @Mapping(target = "registrationExpiry", expression = "java(formatDate(bus.getRegistrationExpiry()))")
    @Mapping(target = "expirationDate", expression = "java(formatDate(bus.getExpirationDate()))")
    @Mapping(target = "registrationExpiringSoon", expression = "java(isRegistrationExpiringSoon(bus))")
    @Mapping(target = "usageExpiringSoon", expression = "java(isUsageExpiringSoon(bus))")
    BusDTO toBusDTO(Bus bus);

    Bus toBus(BusDTO dto);

    default String formatDate(java.time.LocalDate date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    default boolean isRegistrationExpiringSoon(Bus bus) {
        return bus.getRegistrationExpiry() != null &&
                bus.getRegistrationExpiry().isBefore(LocalDate.now().plusDays(30));
    }

    default boolean isUsageExpiringSoon(Bus bus) {
        return bus.getExpirationDate() != null &&
                bus.getExpirationDate().isBefore(LocalDate.now().plusDays(30));
    }
}
