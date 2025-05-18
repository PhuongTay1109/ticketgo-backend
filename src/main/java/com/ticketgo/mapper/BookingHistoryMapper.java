package com.ticketgo.mapper;

import com.ticketgo.dto.BookingHistoryDTO;
import com.ticketgo.projector.BookingHistoryDTOTuple;

import java.util.List;

import static com.ticketgo.util.DateTimeUtils.DATE_TIME_FORMATTER;

public class BookingHistoryMapper {

//    public static BookingHistoryDTO toBookingHistoryDTO(BookingHistoryDTOTuple tuple) {
//        return BookingHistoryDTO.builder()
//                .ticketCode(tuple.getTicketCode())
//                .contactName(tuple.getContactName())
//                .contactEmail(tuple.getContactEmail())
//                .routeName(tuple.getRouteName())
//                .departureDate(tuple.getDepartureDate().format(DATE_FORMATTER))
//                .pickupTime(tuple.getPickupTime().format(DATE_TIME_FORMATTER))
//                .pickupLocation(tuple.getPickupLocation())
//                .dropoffLocation(tuple.getDropoffLocation())
//                .seatNumber(tuple.getSeatNumber())
//                .originalPrice(tuple.getOriginalPrice().toString())
//                .discountedPrice(tuple.getDiscountedPrice() != null ? tuple.getDiscountedPrice().toString() : null)
//                .licensePlate(tuple.getLicensePlate())
//                .status(getVietnameseStatus(tuple.getStatus()))
//                .build();
//    }

    public static BookingHistoryDTO toBookingHistoryDTO(List<BookingHistoryDTOTuple> tuples) {
        BookingHistoryDTOTuple first = tuples.get(0);

        String seatInfos="";
        for (int i = 0; i < tuples.size(); i++) {
            seatInfos += tuples.get(i).getSeatNumber();
            if (i < tuples.size() - 1) {
                seatInfos += " , ";
            }
        }

        BookingHistoryDTO dto = new BookingHistoryDTO();
        dto.setBookingId(first.getBookingId());
        dto.setBookingDate(first.getBookingDate().format(DATE_TIME_FORMATTER));
        dto.setSeatInfos(seatInfos);
        dto.setContactName(first.getContactName());
        dto.setRouteName(first.getRouteName());
        dto.setDepartureDate(first.getDepartureDate().format(DATE_TIME_FORMATTER));
        dto.setPickupTime(first.getPickupTime().format(DATE_TIME_FORMATTER));
        dto.setPickupLocation(first.getPickupLocation());
        dto.setDropoffLocation(first.getDropoffLocation());
        dto.setLicensePlate(first.getLicensePlate());
        dto.setContactEmail(first.getContactEmail());
        dto.setOriginalPrice(first.getOriginalPrice());
        dto.setDiscountedPrice(first.getDiscountedPrice());
        dto.setStatus(getVietnameseStatus(first.getStatus()));
        return dto;
    }

    public static String getVietnameseStatus(String status) {
        return switch (status) {
            case "CONFIRMED" -> "Đã xác nhận";
            case "COMPLETED" -> "Hoàn thành";
            case "CANCELLED" -> "Đã hủy";
            case "REFUNDED" -> "Đã hoàn tiền";
            default -> status;
        };
    }
}

