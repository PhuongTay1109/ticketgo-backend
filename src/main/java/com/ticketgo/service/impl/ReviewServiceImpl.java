package com.ticketgo.service.impl;

import com.ticketgo.dto.ReviewDTO;
import com.ticketgo.entity.Customer;
import com.ticketgo.entity.Review;
import com.ticketgo.entity.Ticket;
import com.ticketgo.entity.User;
import com.ticketgo.exception.AppException;
import com.ticketgo.repository.BookingRepository;
import com.ticketgo.repository.ReviewRepository;
import com.ticketgo.repository.UserRepository;
import com.ticketgo.request.CreateReviewRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.ticketgo.util.DateTimeUtils.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public void createReview(CreateReviewRequest req) {
        Review review = new Review();
        review.setRating(req.getRating());
        review.setComment(req.getComment());

        review.setBooking(bookingRepository.findById(req.getBookingId())
                    .orElseThrow(() -> new AppException("Booking not found", HttpStatus.NOT_FOUND)));
        review.setUser(userRepository.findById(req.getUserId())
                    .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND)));

        reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Long id, Review updatedReview) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new AppException("Review not found", HttpStatus.NOT_FOUND));

        review.setRating(updatedReview.getRating());
        review.setComment(updatedReview.getComment());
        review.setBooking(updatedReview.getBooking());

        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long id) {
       Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new AppException("Review not found", HttpStatus.NOT_FOUND));

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        Customer customer = (Customer) authentication.getPrincipal();

        if (!Objects.equals(customer.getUserId(), review.getUser().getUserId())) {
            throw new AppException("You are not authorized to delete this review", HttpStatus.FORBIDDEN);
        }

        reviewRepository.deleteById(id);
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    @Override
    public ApiPaginationResponse getReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAll(pageable);
        Page<ReviewDTO> dtos = reviews.map(this::toDto);
        ApiPaginationResponse.Pagination pagination = new ApiPaginationResponse.Pagination(
                dtos.getNumber() + 1,
                dtos.getSize(),
                dtos.getTotalPages(),
                dtos.getTotalElements()
        );

        return new ApiPaginationResponse(HttpStatus.OK, "Danh sách đánh giá", dtos.getContent(), pagination);
    }

    private ReviewDTO toDto(Review review) {
        List<Ticket> tickets = review.getBooking().getTickets();

        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(review.getReviewId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setReviewDate(review.getCreatedAt().format(DATE_TIME_FORMATTER));
        dto.setRoute(tickets.get(0).getSchedule().getRoute().getRouteName());
        dto.setTravelDate(tickets.get(0).getSchedule().getDepartureTime().format(DATE_TIME_FORMATTER));

        User user = review.getUser();
        Hibernate.initialize(user); // Force Hibernate to initialize the proxy

        if (user instanceof Customer customer) {
            dto.setUserName(customer.getFullName());
        } else {
            dto.setUserName("Unknown"); // hoặc xử lý fallback
        }

        dto.setUserImg(user.getImageUrl());
        return dto;
    }
}

