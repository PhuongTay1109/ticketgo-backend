package com.ticketgo.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotalPriceCalculationResponse {
    private double totalPrice;
    private double unitPrice;
    private int quantity;
    private List<String> seatNumbers;
}
