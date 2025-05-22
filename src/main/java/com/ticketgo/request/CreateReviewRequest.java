package com.ticketgo.request;

import lombok.Data;

@Data
public class CreateReviewRequest {
    private Long bookingId;
    private Integer rating;
    private Long userId;
    private String comment;
}
