package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersion.V1 + "/amenities")
public class AmenityController {
    private final AmenityService amenityService;

    @GetMapping("")
    public ApiResponse getAmenities() {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy các tiện ích của công ty thành công",
                amenityService.getAmenities()
        );
    }
}
