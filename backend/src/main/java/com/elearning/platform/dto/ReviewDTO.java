package com.elearning.platform.dto;

import java.time.LocalDateTime;

public class ReviewDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private String reviewerName;
    private LocalDateTime createdAt;

    public ReviewDTO() {
    }

    public ReviewDTO(Long id, Integer rating, String comment, String reviewerName, LocalDateTime createdAt) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.reviewerName = reviewerName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
