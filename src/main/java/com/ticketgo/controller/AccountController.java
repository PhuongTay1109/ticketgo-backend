package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.request.AccountListRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersion.V1 + "/accounts")
public class AccountController {
    private final AccountService accountService;

    @PutMapping("/{userId}/change-lock-status")
    public ApiResponse toggleLockStatus(@PathVariable Long userId) {
        accountService.changeLockStatus(userId);
        return new ApiResponse(
                HttpStatus.OK,
                "Thay đổi trạng thái tài khoản thành công",
                null
        );
    }

    @GetMapping
    public ApiPaginationResponse getAccounts(@Valid AccountListRequest req) {
        return accountService.getAccounts(req);
    }
}
