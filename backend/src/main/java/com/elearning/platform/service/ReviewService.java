package com.elearning.platform.service;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Review;
import com.elearning.platform.domain.UserAccount;
import com.elearning.platform.dto.ReviewCreateRequest;
import com.elearning.platform.dto.ReviewDTO;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.repository.ReviewRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final com.elearning.platform.repository.EnrollmentRepository enrollmentRepository;
    private final UserService userService;

    public ReviewService(ReviewRepository reviewRepository,
                         CourseRepository courseRepository,
                         com.elearning.platform.repository.EnrollmentRepository enrollmentRepository,
                         UserService userService) {
        this.reviewRepository = reviewRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsForCourse(Long courseId) {
        return reviewRepository.findByCourseId(courseId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDTO addReview(Long courseId, Long userId, ReviewCreateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        UserAccount user = userService.findOne(userId);

        // Security check: Only enrolled students can review
        if (!enrollmentRepository.existsByLearnerIdAndCourseId(userId, courseId)) {
            throw new IllegalStateException("You must be enrolled in a course to review it");
        }

        Review review = new Review(request.getRating(), request.getComment(), course, user);
        Review saved = reviewRepository.save(review);
        return toDTO(saved);
    }

    private ReviewDTO toDTO(Review review) {
        return new ReviewDTO(review.getId(), review.getRating(), review.getComment(),
                review.getUser().getName(), review.getCreatedAt());
    }
}
