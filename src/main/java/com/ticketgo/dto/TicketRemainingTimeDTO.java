package com.ticketgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketRemainingTimeDTO {
    private final Long remainingTime;
}
