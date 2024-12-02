package com.ticketgo.mapper;

import com.ticketgo.dto.BookingHistoryDTO;
import com.ticketgo.dto.BookingHistoryDTOTuple;

import java.time.format.DateTimeFormatter;

public class BookingHistoryMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

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
        switch (status) {
            case "CONFIRMED":
                return "Đã xác nhận";
            case "COMPLETED":
                return "Hoàn thành";
            case "CANCELLED":
                return "Đã hủy";
            case "REFUNDED":
                return "Đã hoàn tiền";
            default:
                return status;
        }
    }
}

