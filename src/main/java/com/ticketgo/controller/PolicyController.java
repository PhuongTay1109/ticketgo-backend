package com.ticketgo.controller;

import com.ticketgo.dto.PolicyDTO;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/policies")
public class PolicyController {
    private final PolicyService policyService;

    @GetMapping("")
    public ApiResponse getPolicies() {
        List<PolicyDTO> resp = policyService.getPolicies();
        return new ApiResponse(HttpStatus.OK, "Lấy các chính sách của công ty thành công", resp);
    }
}
