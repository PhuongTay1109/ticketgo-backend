package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.request.UserUpdateRequest;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiVersion.V1 + "/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse getUser() {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thông tin cá nhân thành công",
                userService.getUserDetails()
        );
    }

    @PostMapping()
    public ApiResponse updateUser( @RequestBody UserUpdateRequest request) {
        userService.updateUser(request);
        return new ApiResponse(
                HttpStatus.OK,
                "Cập nhật thông tin cá nhân thành công",
                null
        );
    }

    @PutMapping("/{userId}/change-lock-status")
    @PreAuthorize("hasRole('BUS_COMPANY')")
    public ApiResponse toggleLockStatus(@PathVariable Long userId) {
        userService.changeLockStatus(userId);
        return new ApiResponse(
                HttpStatus.OK,
                "Thay đổi trạng thái tài khoản thành công",
                null
        );
    }
}
