package com.ticketgo.mapper;

import com.ticketgo.dto.BookingInfoDTO;
import com.ticketgo.projector.BookingInfoDTOTuple;
import lombok.extern.slf4j.Slf4j;

import static com.ticketgo.util.DateTimeUtils.DATE_TIME_FORMATTER;

@Slf4j
public class BookingInfoMapper {

    public static BookingInfoDTO toBookingInfoDTO(BookingInfoDTOTuple tuple) {
        log.info("Departure date: {}", tuple.getDepartureDate());
        return BookingInfoDTO.builder()
                .bookingId(tuple.getBookingId())
                .bookingDate(tuple.getBookingDate().format(DATE_TIME_FORMATTER))
                .ticketCode(tuple.getTicketCode())
                .contactName(tuple.getContactName())
                .contactEmail(tuple.getContactEmail())
                .routeName(tuple.getRouteName())
                .departureDate(tuple.getDepartureDate().format(DATE_TIME_FORMATTER))
                .pickupTime(tuple.getPickupTime().format(DATE_TIME_FORMATTER))
                .pickupLocation(tuple.getPickupLocation())
                .dropoffLocation(tuple.getDropoffLocation())
                .seatNumber(tuple.getSeatNumber())
                .price(tuple.getPrice().toString())
                .licensePlate(tuple.getLicensePlate())
                .build();
    }
}
