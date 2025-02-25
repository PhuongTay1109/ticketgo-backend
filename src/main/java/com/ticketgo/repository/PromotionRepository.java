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

import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Page<Promotion> findByStatus(PromotionStatus status, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Promotion p
        SET p.isDeleted = true,
            p.status = 'INACTIVE'
        WHERE p.promotionId = :promotionId
    """)
    void softDelete(@Param("promotionId") Long promotionId);

    Promotion findByPromotionId(Long id);

    Optional<Promotion> findByDiscountCode(String discountCode);
}
