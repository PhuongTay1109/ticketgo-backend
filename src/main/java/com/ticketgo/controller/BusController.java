package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.dto.BusDTO;
import com.ticketgo.request.BusListRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.BusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersion.V1 + "/buses")
public class BusController {

    private final BusService busService;

    @GetMapping()
    public ApiPaginationResponse getBuses(@Valid BusListRequest request) {
        return busService.getBuses(request);
    }

    @GetMapping("/{id}")
    public ApiResponse getBusById(@PathVariable long id) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thông tin xe thành công",
                busService.getBusById(id)
        );
    }

    @PostMapping
    public ApiResponse createBus(@RequestBody BusDTO dto) {
        busService.createBus(dto);
        return new ApiResponse(
                HttpStatus.CREATED,
                "Tạo xe thành công",
                null
        );
    }

    @PostMapping("/{id}")
    public ApiResponse updateBus(@PathVariable long id,
                                 @RequestBody BusDTO dto) {
        busService.updateBus(id, dto);
        return new ApiResponse(
                HttpStatus.OK,
                "Cập nhật xe thành công",
                null
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteBus(@PathVariable long id) {
        busService.deleteBus(id);
        return new ApiResponse(
                HttpStatus.OK,
                "Xóa xe thành công",
                null
        );
    }
}

