package com.ticketgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavedInProgressInfo {
    private long bookingId;
    private Double paymentAmount;
}
