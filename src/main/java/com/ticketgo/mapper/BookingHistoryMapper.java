package com.ticketgo.mapper;

import com.ticketgo.dto.BookingHistoryDTO;
import com.ticketgo.projector.BookingHistoryDTOTuple;

import static com.ticketgo.util.DateTimeUtils.DATE_FORMATTER;
import static com.ticketgo.util.DateTimeUtils.DATE_TIME_FORMATTER;

public class BookingHistoryMapper {


    public static BookingHistoryDTO toBookingHistoryDTO(BookingHistoryDTOTuple tuple) {
        return BookingHistoryDTO.builder()
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
                .status(getVietnameseStatus(tuple.getStatus()))
                .build();
    }

    private static String getVietnameseStatus(String status) {
        return switch (status) {
            case "CONFIRMED" -> "Đã xác nhận";
            case "COMPLETED" -> "Hoàn thành";
            case "CANCELLED" -> "Đã hủy";
            case "REFUNDED" -> "Đã hoàn tiền";
            default -> status;
        };
    }
}

