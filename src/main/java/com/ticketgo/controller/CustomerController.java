package com.ticketgo.controller;

import com.ticketgo.dto.CustomerContactInfoDTO;
import com.ticketgo.dto.response.ApiResponse;
import com.ticketgo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final UserService userService;

    @GetMapping("/contact-info")
    public ApiResponse getCustomerContactInfo() {
        CustomerContactInfoDTO resp = userService.getCustomerContactIno();
        return new ApiResponse(HttpStatus.OK, null, resp);
    }
}
