package com.elearning.platform.controller;

import com.elearning.platform.dto.ReviewCreateRequest;
import com.elearning.platform.dto.ReviewDTO;
import com.elearning.platform.security.UserDetailsImpl;
import com.elearning.platform.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses/{id}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ReviewDTO> getReviews(@PathVariable Long id) {
        return reviewService.getReviewsForCourse(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STUDENT')")
    public ReviewDTO addReview(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                               @Valid @RequestBody ReviewCreateRequest request) {
        return reviewService.addReview(id, userDetails.getId(), request);
    }
}
