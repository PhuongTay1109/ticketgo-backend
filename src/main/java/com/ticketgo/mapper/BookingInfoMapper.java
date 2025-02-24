package com.ticketgo.mapper;

import com.ticketgo.dto.BookingInfoDTO;
import com.ticketgo.projector.BookingInfoDTOTuple;

import static com.ticketgo.util.DateTimeUtils.DATE_FORMATTER;
import static com.ticketgo.util.DateTimeUtils.DATE_TIME_FORMATTER;

public class BookingInfoMapper {

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
