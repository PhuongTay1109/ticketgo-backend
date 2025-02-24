package com.ticketgo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ticketgo.enums.PromotionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class PromotionDTO {
    private String description;
    private Integer discountPercentage;
    private String discountCode;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private PromotionStatus status;
}
