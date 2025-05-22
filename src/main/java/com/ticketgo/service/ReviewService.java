package com.ticketgo.service;

import com.ticketgo.entity.Review;
import com.ticketgo.request.CreateReviewRequest;
import com.ticketgo.response.ApiPaginationResponse;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    void createReview(CreateReviewRequest req);
    Review updateReview(Long id, Review review);
    void deleteReview(Long id);
    Review getReviewById(Long id);
    ApiPaginationResponse getReviews(Pageable pageable);
}

