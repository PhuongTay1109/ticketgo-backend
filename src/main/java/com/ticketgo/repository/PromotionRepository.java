package com.ticketgo.repository;

import com.ticketgo.entity.Promotion;
import com.ticketgo.enums.PromotionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Page<Promotion> findByStatus(PromotionStatus status, Pageable pageable);
    Promotion findByPromotionIdAndStatus(Long promotionId, PromotionStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Promotion p
        SET p.isDeleted = true
        WHERE p.promotionId = :promotionId
    """)
    void softDelete(@Param("promotionId") Long promotionId);

    Promotion findByPromotionId(Long id);
}
