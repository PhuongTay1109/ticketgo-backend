package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.RouteStopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersion.V1 + "/route-stops")
public class RouteStopController {
    private final RouteStopService routeStopService;

    @GetMapping()
    public ApiResponse getRouteStops(@RequestParam long scheduleId) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy các trạm dừng thành công",
                routeStopService.getRouteStops(scheduleId)
        );
    }

    @GetMapping("/pickup")
    public ApiResponse getPickupStops(@RequestParam long scheduleId) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy các trạm đón thành công",
                routeStopService.getPickupStops(scheduleId)
        );
    }

    @GetMapping("/dropoff")
    public ApiResponse getDropoffStops(@RequestParam long scheduleId) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy các trạm trả thành công",
                routeStopService.getDropoffStops(scheduleId)
        );
    }
}
