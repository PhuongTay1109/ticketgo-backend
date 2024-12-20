package com.ticketgo.service;

import com.ticketgo.dto.BookingInfoDTO;
import com.ticketgo.dto.RevenueStatisticsDTO;
import com.ticketgo.dto.request.PaymentRequest;
import com.ticketgo.dto.response.ApiPaginationResponse;
import com.ticketgo.dto.response.TripInformationResponse;
import com.ticketgo.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    TripInformationResponse getTripInformation(long pickupStopId, long dropoffStopId, long scheduleId);
    long saveInProgressBooking(PaymentRequest request);

    void setConfirmedVNPayBooking(long bookingId);
    void setFailedVNPayBooking(long bookingId);
    Booking findById(long bookingId);

    List<BookingInfoDTO> getBookingInfoList(long bookingId);

//    List<BookingHistoryDTO> getBookingHistoryForCustomer();
    ApiPaginationResponse getBookingHistoryForCustomer(int pageNumber, int pageSize);

    List<RevenueStatisticsDTO> getDailyRevenueStatistics(LocalDateTime startDate, LocalDateTime endDate);

    List<RevenueStatisticsDTO> getMonthlyRevenueStatistics(int year);

    List<RevenueStatisticsDTO> getRevenueStatisticsByYear(int year);
}
