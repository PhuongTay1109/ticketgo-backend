package com.ticketgo.controller;

import com.ticketgo.constant.ApiVersion;
import com.ticketgo.request.CreateReviewRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.response.ApiResponse;
import com.ticketgo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( ApiVersion.V1 + "/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ApiResponse create(@RequestBody CreateReviewRequest req) {
        reviewService.createReview(req);
        return new ApiResponse(
                HttpStatus.OK,
                "Cảm ơn bạn đã đánh giá!",
                null
        );
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
    public ApiResponse delete(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return new ApiResponse(
                HttpStatus.OK,
                "Xóa đánh giá thành công.",
                null
        );
    }
}
