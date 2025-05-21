package com.ticketgo.service;

import com.ticketgo.dto.ReviewDTO;
import com.ticketgo.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    Review createReview(Review review);
    Review updateReview(Long id, Review review);
    void deleteReview(Long id);
    Review getReviewById(Long id);
    Page<ReviewDTO> getReviews(Pageable pageable);
}

