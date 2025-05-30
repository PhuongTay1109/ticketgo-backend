package com.ticketgo.service;

import com.ticketgo.dto.*;
import com.ticketgo.projector.CustomerInfoDTOTuple;
import com.ticketgo.request.CancelBookingRequest;
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
    SavedInProgressInfo saveInProgressBooking(PaymentRequest request, long scheduleId);

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

    List<CustomerInfoDTOTuple> getPassengerInfoByScheduleId(Long scheduleId);

    void cancelBooking(CancelBookingRequest req);

    ApiPaginationResponse getAllBookingHistory(
            int pageNumber,
            int pageSize,
            Long bookingId,
            String contactName,
            String contactEmail,
            String routeName,
            String status,
            String refundStatus,
            String fromDate,
            String toDate
    );

    void updateBookingRefundStatus(Long bookingsId);

    ComprehensiveStatisticsDTO getComprehensiveStatisticsDaily(LocalDateTime startDate, LocalDateTime endDate);

    ComprehensiveStatisticsDTO getComprehensiveStatisticsMonthly(int year);

    Object getComprehensiveStatisticsYearly(int year);
}
