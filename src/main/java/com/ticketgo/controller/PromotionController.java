package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.dto.PromotionDTO;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersion.V1 + "/promotions")
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping("/{id}")
    public ApiResponse getPromotion(@PathVariable long id) {
        return new ApiResponse(
                HttpStatus.OK,
                "Lấy thông tin khuyến mãi thành công",
                promotionService.getPromotion(id)
        );
    }

    @PostMapping
    public ApiResponse createPromotion(@RequestBody PromotionDTO dto) {
        promotionService.createPromotion(dto);
        return new ApiResponse(
                HttpStatus.CREATED,
                "Tạo khuyến mãi thành công",
                null
        );
    }

    @PostMapping("/{id}")
    public ApiResponse updatePromotion(@PathVariable long id,
                                       @RequestBody PromotionDTO dto) {
        promotionService.updatePromotion(id, dto);
        return new ApiResponse(
                HttpStatus.OK,
                "Cập nhật khuyến mãi thành công",
                null
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse deletePromotion(@PathVariable long id) {
        promotionService.deletePromotion(id);
        return new ApiResponse(
                HttpStatus.OK,
                "Xóa khuyến mãi thành công",
                null
        );
    }
}
