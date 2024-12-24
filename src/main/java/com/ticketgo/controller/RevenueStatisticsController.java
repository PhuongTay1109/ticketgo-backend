package com.ticketgo.controller;

import com.ticketgo.dto.RevenueStatisticsDTO;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/revenues")
@RequiredArgsConstructor
public class RevenueStatisticsController {

    private final BookingService bookingService;

    @GetMapping("/statistics-daily")
    public ApiResponse getRevenueStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<RevenueStatisticsDTO> resp = bookingService.getDailyRevenueStatistics(startDateTime, endDateTime);
        return new ApiResponse(HttpStatus.OK, "Lấy thống kê theo ngày thành công", resp);
    }

    @GetMapping("/statistics-monthly")
    public ApiResponse getRevenueStatisticsByMonth(@RequestParam int year) {
        List<RevenueStatisticsDTO> resp = bookingService.getMonthlyRevenueStatistics(year);
        return new ApiResponse(HttpStatus.OK, "Lấy thống kê theo tháng thành công", resp);
    }

    @GetMapping("/statistics-yearly")
    public ApiResponse getRevenueStatisticsByYear(
            @RequestParam int year) {
        List<RevenueStatisticsDTO> resp = bookingService.getRevenueStatisticsByYear(year);
        return new ApiResponse(HttpStatus.OK, "Lấy thống kê theo năm thành công", resp);
    }

}
