package com.ticketgo.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceEstimationResponse {
    private double totalPrice;
    private double unitPrice;
    private int quantity;
    private List<String> seatNumbers;
}
