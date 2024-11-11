package com.ticketgo.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class TotalPriceCalculationRequest {
    private List<Long> seatIds;
    private Long scheduleId;
}
