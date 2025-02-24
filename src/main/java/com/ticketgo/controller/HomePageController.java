package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.HomePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersion.V1 + "/homepage")
public class HomePageController {
    private final HomePageService homePageService;

    @GetMapping("")
    public ApiResponse getHomePageInfo() {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thông tin trang chủ thành công",
                homePageService.getHomePageInfo()
        );
    }
}
