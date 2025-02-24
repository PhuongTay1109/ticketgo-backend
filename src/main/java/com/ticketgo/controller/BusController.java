package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.request.BusListRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.service.BusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersion.V1 + "/buses")
public class BusController {

    private final BusService busService;

    @GetMapping()
    public ApiPaginationResponse getAllBuses(@Valid BusListRequest request) {
        return busService.getAllBuses(request);
    }
}

