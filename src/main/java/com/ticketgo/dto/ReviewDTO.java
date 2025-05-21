package com.ticketgo.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long reviewId;
    private Integer rating;
    private String comment;
    private String travelDate;
    private String route;
    private String reviewDate;
}
