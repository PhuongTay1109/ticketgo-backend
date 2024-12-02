package com.ticketgo.mapper;

import com.ticketgo.dto.BookingInfoDTO;
import com.ticketgo.dto.BookingInfoDTOTuple;

import java.time.format.DateTimeFormatter;

public class BookingInfoMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public static BookingInfoDTO toBookingInfoDTO(BookingInfoDTOTuple tuple) {
        return BookingInfoDTO.builder()
                .ticketCode(tuple.getTicketCode())
                .contactName(tuple.getContactName())
                .contactEmail(tuple.getContactEmail())
                .routeName(tuple.getRouteName())
                .departureDate(tuple.getDepartureDate().format(DATE_FORMATTER))
                .pickupTime(tuple.getPickupTime().format(DATE_TIME_FORMATTER))
                .pickupLocation(tuple.getPickupLocation())
                .dropoffLocation(tuple.getDropoffLocation())
                .seatNumber(tuple.getSeatNumber())
                .price(tuple.getPrice().toString())
                .licensePlate(tuple.getLicensePlate())
                .build();
    }
}
