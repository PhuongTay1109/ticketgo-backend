package com.ticketgo.controller;

import com.ticketgo.dto.UserDTO;
import com.ticketgo.dto.request.UserUpdateRequest;
import com.ticketgo.dto.response.ApiResponse;
import com.ticketgo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse getUser() {
        UserDTO userResponseDTO = userService.getUserDetails();
        return new ApiResponse(HttpStatus.OK, null, userResponseDTO);
    }

    @PostMapping()
    public ApiResponse updateUser( @RequestBody UserUpdateRequest request) {
        userService.updateUser(request);
        return new ApiResponse(HttpStatus.OK, "Cập nhật thông tin cá nhân thành công", null);
    }
}
