package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersion.V1 + "/buses")
public class BusController {

    private final BusService busService;

    @GetMapping()
    public ApiPaginationResponse getAllBuses(@RequestParam int pageNumber,
                                             @RequestParam int pageSize) {
        return busService.getAllBuses(pageNumber, pageSize);
    }
}
