package com.ticketgo.service.impl;

import com.ticketgo.dto.HomePageInfoDTO;
import com.ticketgo.entity.BusCompany;
import com.ticketgo.service.HomePageService;
import com.ticketgo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomePageServiceImpl implements HomePageService {
    private final UserService userService;

    @Override
    public HomePageInfoDTO getHomePageInfo() {
        BusCompany admin = (BusCompany) userService.findByEmail("admin@gmail.com");
        return HomePageInfoDTO.builder()
                .bannerUrl(admin.getBannerUrl())
                .description(admin.getDescription())
                .build();
    }
}
