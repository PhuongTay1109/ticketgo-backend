package com.ticketgo.service;

import com.ticketgo.dto.PromotionDTO;

public interface PromotionService {
    void createPromotion(PromotionDTO dto);
    PromotionDTO getPromotion(Long id);
    void updatePromotion(Long id, PromotionDTO dto);
    void deletePromotion(Long id);
}
