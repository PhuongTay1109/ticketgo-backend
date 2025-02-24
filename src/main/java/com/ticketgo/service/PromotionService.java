package com.ticketgo.service;

import com.ticketgo.dto.PromotionDTO;
import com.ticketgo.request.PromotionListRequest;
import com.ticketgo.response.ApiPaginationResponse;

public interface PromotionService {
    void createPromotion(PromotionDTO dto);
    PromotionDTO getPromotionById(Long id);
    ApiPaginationResponse getPromotions(PromotionListRequest req, boolean isActiveFilter);
    void updatePromotion(Long id, PromotionDTO dto);
    void deletePromotion(Long id);
}
