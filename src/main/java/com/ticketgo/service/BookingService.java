package com.ticketgo.service;

import com.ticketgo.dto.*;
import com.ticketgo.request.PaymentRequest;
import com.ticketgo.request.SaveBookingInfoRequest;
import com.ticketgo.request.SaveContactInfoRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.response.TripInformationResponse;
import com.ticketgo.entity.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    TripInformationResponse getTripInformation(long pickupStopId, long dropoffStopId, long scheduleId);
    SavedInProgressInfo saveInProgressBooking(PaymentRequest request);

    void setConfirmedVNPayBooking(long bookingId);
    void setFailedVNPayBooking(long bookingId);
    Booking findById(long bookingId);

    List<BookingInfoDTO> getBookingInfoList(long bookingId);

//    List<BookingHistoryDTO> getBookingHistoryForCustomer();
    ApiPaginationResponse getBookingHistoryForCustomer(int pageNumber, int pageSize);

    List<RevenueStatisticsDTO> getDailyRevenueStatistics(LocalDateTime startDate, LocalDateTime endDate);

    List<RevenueStatisticsDTO> getMonthlyRevenueStatistics(int year);

    List<RevenueStatisticsDTO> getRevenueStatisticsByYear(int year);

    void saveBookingInfo(SaveBookingInfoRequest request);

    BookingConfirmDTO getBookingInfo(Long scheduleId);

    BookingStepDTO getBookingStep(Long scheduleId);

    void saveCustomerContactInfo(SaveContactInfoRequest request);

    SaveContactInfoRequest getCustomerContactInfo(long scheduleId);
}
