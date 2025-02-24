package com.ticketgo.mapper;

import com.ticketgo.dto.PromotionDTO;
import com.ticketgo.entity.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PromotionMapper {
    PromotionMapper INSTANCE = Mappers.getMapper(PromotionMapper.class);

    PromotionDTO toPromotionDTO(Promotion promotion);
    Promotion toPromotion(PromotionDTO promotionDTO);
}
