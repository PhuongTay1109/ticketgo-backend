package com.ticketgo.controller;

import com.ticketgo.dto.AmenityDTO;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/amenities")
public class AmenityController {
    private final AmenityService amenityService;

    @GetMapping("")
    public ApiResponse getAmenities() {
        List<AmenityDTO> resp = amenityService.getAmenities();
        return new ApiResponse(HttpStatus.OK, "Lấy các tiện ích của công ty thành công", resp);
    }
}
