package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.request.ScheduleCreateRequest;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.response.BusScheduleResponse;
import com.ticketgo.response.DriverScheduleResponse;
import com.ticketgo.service.BookingService;
import com.ticketgo.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersion.V1 + "/schedules")
@Slf4j
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('BUS_COMPANY')")
    public ApiResponse create(@RequestBody ScheduleCreateRequest req) {
        scheduleService.create(req);
        return new ApiResponse(
                HttpStatus.CREATED,
                "Tạo chuyến xe thành công",
                null
        );
    }

    @GetMapping("/{scheduleId}/customers")
    @PreAuthorize("hasRole('BUS_COMPANY')")
    public ApiResponse getCustomerInfo(@PathVariable Long scheduleId) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thông tin khách hàng thành công",
                bookingService.getPassengerInfoByScheduleId(scheduleId)
        );
    }

    @PutMapping("/{scheduleId}/status")
    @PreAuthorize("hasRole('BUS_COMPANY')")
    public ApiResponse updateScheduleStatus(@PathVariable Long scheduleId,
                                             @RequestParam("status") String status) {
        scheduleService.updateScheduleStatus(scheduleId, status);
        return new ApiResponse(
                HttpStatus.OK,
                "Cập nhật trạng thái chuyến xe thành công",
                null
        );
    }

    @GetMapping("/bus/{busId}")
    @PreAuthorize("hasRole('BUS_COMPANY')")
    public ResponseEntity<BusScheduleResponse> getBusSchedule(
            @PathVariable Long busId,
            @RequestParam String month) {

        try {
            YearMonth yearMonth = YearMonth.parse(month); // Format: yyyy-MM
            BusScheduleResponse response = scheduleService.getBusScheduleForMonth(busId, yearMonth);
            return ResponseEntity.ok(response);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<DriverScheduleResponse> getDriverSchedule(
            @PathVariable Long driverId,
            @RequestParam String month) {

        try {
            YearMonth yearMonth = YearMonth.parse(month); // Format: yyyy-MM
            DriverScheduleResponse response = scheduleService.getDriverScheduleForMonth(driverId, yearMonth);
            return ResponseEntity.ok(response);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{scheduleId}/driver")
    @PreAuthorize("hasRole('BUS_COMPANY')")
    public ApiResponse updateDriverForSchedule(
            @PathVariable Long scheduleId,
            @RequestParam Long driverId) {

        scheduleService.updateDriverForSchedule(scheduleId, driverId);

        return new ApiResponse(
                HttpStatus.OK,
                "Cập nhật tài xế cho chuyến xe thành công",
                null
        );
    }

    @PutMapping("/{scheduleId}/bus")
    @PreAuthorize("hasRole('BUS_COMPANY')")
    public ApiResponse updateBusForSchedule(
            @PathVariable Long scheduleId,
            @RequestParam Long busId) {

        scheduleService.updateBusForSchedule(scheduleId, busId);

        return new ApiResponse(
                HttpStatus.OK,
                "Cập nhật xe cho chuyến xe thành công",
                null
        );
    }

}
