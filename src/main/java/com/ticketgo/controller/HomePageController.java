package com.ticketgo.controller;

import com.ticketgo.dto.HomePageInfoDTO;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.HomePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/homepage")
public class HomePageController {
    private final HomePageService homePageService;

    @GetMapping("")
    public ApiResponse getHomePageInfo() {
        HomePageInfoDTO resp = homePageService.getHomePageInfo();
        return new ApiResponse(HttpStatus.OK, "Lấy thông tin trang chủ thành công", resp);
    }
}
