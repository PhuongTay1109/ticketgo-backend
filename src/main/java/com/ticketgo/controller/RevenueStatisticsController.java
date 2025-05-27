package com.ticketgo.controller;

import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class RevenueStatisticsController {

    private final BookingService bookingService;

    @GetMapping("/api/v1/revenues/statistics-daily")
    public ApiResponse getRevenueStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thống kê theo ngày thành công",
                bookingService.getDailyRevenueStatistics(startDateTime, endDateTime)
        );
    }

    @GetMapping("/api/v1/revenues/statistics-monthly")
    public ApiResponse getRevenueStatisticsByMonth(@RequestParam int year) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thống kê theo tháng thành công",
                bookingService.getMonthlyRevenueStatistics(year)
        );
    }

    @GetMapping("/api/v1/revenues/statistics-yearly")
    public ApiResponse getRevenueStatisticsByYear(@RequestParam int year) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thống kê theo năm thành công",
                bookingService.getRevenueStatisticsByYear(year)
        );
    }

    @GetMapping("/api/v2/revenues/statistics-daily")
    public ApiResponse getRevenueStatisticsV2(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thống kê theo ngày thành công",
                bookingService.getComprehensiveStatisticsDaily(startDateTime, endDateTime)
        );
    }

    @GetMapping("/api/v2/revenues/statistics-monthly")
    public ApiResponse getRevenueStatisticsByMonthV2(@RequestParam int year) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thống kê theo tháng thành công",
                bookingService.getComprehensiveStatisticsMonthly(year)
        );
    }

    @GetMapping("/api/v2/revenues/statistics-yearly")
    public ApiResponse getRevenueStatisticsByYearV2(@RequestParam int year) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thống kê theo năm thành công",
                bookingService.getComprehensiveStatisticsYearly(year)
        );
    }
}
