package com.ticketgo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PriceEstimationResponse {
    private double totalPrice;
    private double unitPrice;
    private int quantity;
    private List<String> seatNumbers;
}
