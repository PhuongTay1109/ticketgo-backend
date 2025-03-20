package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.request.DriverCreateRequest;
import com.ticketgo.request.DriverListRequest;
import com.ticketgo.request.DriverUpdateRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersion.V1 + "/drivers")
public class DriverController {
    private final DriverService driverService;

    @GetMapping()
    public ApiPaginationResponse list(@Valid DriverListRequest request) {
        return driverService.list(request);
    }

    @GetMapping("/{id}")
    public ApiResponse getDriverForSchedule(@PathVariable long id) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thông tin tài xế thành công",
                driverService.get(id)
        );
    }

//    @GetMapping("/available")
//    public ApiResponse getAvailableDrivers(@RequestParam LocalDateTime departureTime,
//                                           @RequestParam LocalDateTime arrivalTime) {
//        return new ApiResponse(
//                HttpStatus.OK,
//                "Danh sách tài xế khả dụng",
//                driverService.getAvailableDrivers(departureTime, arrivalTime)
//        );
//    }


    @GetMapping("/schedule")
    public ApiResponse getDriverForSchedule(@RequestParam Long scheduleId) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thông tin tài xế thành công",
                driverService.getDriverForSchedule(scheduleId)
        );
    }

    @PostMapping
    public ApiResponse add(@RequestBody DriverCreateRequest request) {
        driverService.add(request);
        return new ApiResponse(
                HttpStatus.CREATED,
                "Tạo tài xế thành công",
                null
        );
    }

    @PutMapping("/{id}")
    public ApiResponse updateBus(@PathVariable long id,
                                 @RequestBody DriverUpdateRequest req) {
        driverService.update(id, req);
        return new ApiResponse(
                HttpStatus.OK,
                "Cập nhật tài xế thành công",
                null
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable long id) {
        driverService.delete(id);
        return new ApiResponse(
                HttpStatus.OK,
                "Xóa tài xế thành công",
                null
        );
    }
}
