package com.ticketgo.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class PriceEstimationRequest {
    private List<String> ticketCodes;
}
