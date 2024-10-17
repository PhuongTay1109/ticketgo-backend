package com.ticketgo.controller;

import com.ticketgo.dto.UserDTO;
import com.ticketgo.dto.response.ApiResponse;
import com.ticketgo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
