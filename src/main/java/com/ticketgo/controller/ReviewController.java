package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.request.CreateReviewRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( ApiVersion.V1 + "/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateReviewRequest req) {
        reviewService.createReview(req);
        return ResponseEntity.status(201).build();
    }

    @GetMapping
    public ApiPaginationResponse getAllReviews(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        return reviewService.getReviews(pageable);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Review> getById(@PathVariable Long id) {
//        return ResponseEntity.ok(reviewService.getReviewById(id));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Review> update(@PathVariable Long id, @RequestBody Review review) {
//        return ResponseEntity.ok(reviewService.updateReview(id, review));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
