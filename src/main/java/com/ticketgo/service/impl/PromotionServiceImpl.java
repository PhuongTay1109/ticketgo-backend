package com.ticketgo.service.impl;

import com.ticketgo.dto.PromotionDTO;
import com.ticketgo.entity.Promotion;
import com.ticketgo.enums.PromotionStatus;
import com.ticketgo.exception.AppException;
import com.ticketgo.mapper.PromotionMapper;
import com.ticketgo.repository.PromotionRepository;
import com.ticketgo.request.PromotionListRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.service.PromotionService;
import com.ticketgo.util.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepo;

    @Override
    public void createPromotion(PromotionDTO dto) {
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new AppException("Ngày bắt đầu không thể sau ngày kết thúc", HttpStatus.BAD_REQUEST);
        }
        Promotion promotion = PromotionMapper.INSTANCE.toPromotion(dto);
        promotion.setStatus(PromotionStatus.INACTIVE);
        promotionRepo.save(promotion);
    }

    @Override
    public PromotionDTO getPromotionById(Long id) {
        Promotion promotion = promotionRepo.findByPromotionIdAndStatus(id, PromotionStatus.ACTIVE);
        if (promotion != null) {
            return PromotionMapper.INSTANCE.toPromotionDTO(promotion);
        }
        throw new AppException("Không tìm thấy khuyến mãi", HttpStatus.NOT_FOUND);
    }

    @Override
    public ApiPaginationResponse getPromotions(PromotionListRequest req, boolean isActiveFilter) {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, req.buildSort());
        Page<Promotion> promotionPage;

        if (isActiveFilter) {
            promotionPage = promotionRepo.findByStatus(PromotionStatus.ACTIVE, pageable);
        } else {
            promotionPage = promotionRepo.findAll(pageable);
        }

        ApiPaginationResponse.Pagination pagination = new ApiPaginationResponse.Pagination(
                promotionPage.getNumber() + 1,
                promotionPage.getSize(),
                promotionPage.getTotalPages(),
                promotionPage.getTotalElements()
        );

        return new ApiPaginationResponse(
                HttpStatus.OK,
                "Danh sách khuyến mãi",
                promotionPage.getContent().stream()
                        .map(PromotionMapper.INSTANCE::toPromotionDTO)
                        .collect(Collectors.toList()),
                pagination
        );
    }

    @Override
    public void updatePromotion(Long id, PromotionDTO dto) {
        Promotion promotion = promotionRepo.findByPromotionId(id);
        if (promotion == null) {
            throw new AppException("Không tìm thấy khuyến mãi", HttpStatus.NOT_FOUND);
        }
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new AppException("Ngày bắt đầu không thể sau ngày kết thúc", HttpStatus.BAD_REQUEST);
        }
        ObjectUtils.copyProperties(dto, promotion);
        promotionRepo.save(promotion);
    }

    @Override
    @Transactional
    public void deletePromotion(Long id) {
        promotionRepo.softDelete(id);
    }
}
