package com.elearning.platform.dto;

import java.time.LocalDateTime;

public class BookmarkDTO {
    private Long courseId;
    private String courseTitle;
    private LocalDateTime savedAt;

    public BookmarkDTO() {}

    public BookmarkDTO(Long courseId, String courseTitle, LocalDateTime savedAt) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.savedAt = savedAt;
    }

    public Long getCourseId() { return courseId; }
    public String getCourseTitle() { return courseTitle; }
    public LocalDateTime getSavedAt() { return savedAt; }
}
