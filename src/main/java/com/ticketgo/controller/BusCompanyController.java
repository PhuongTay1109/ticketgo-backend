package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.dto.BusCompanyDto;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.BusCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiVersion.V1 + "/bus-companies")
public class BusCompanyController {
    private final BusCompanyService busCompanyService;

    @GetMapping
    public ApiResponse getBusCompanyById() {
        BusCompanyDto busCompany = busCompanyService.getBusCompanyById(1L);
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thông tin nhà xe thành công",
                busCompany
        );
    }

    @PutMapping
    public ApiResponse updateBusCompany(@RequestBody BusCompanyDto updatedInfo) {
        busCompanyService.updateBusCompany(1L, updatedInfo);
        return new ApiResponse(
                HttpStatus.OK,
                "Cập nhật thông tin nhà xe thành công",
                null
        );
    }
}
