package com.elearning.platform.dto;

import java.time.LocalDate;
import java.util.List;

public class InstructorProfileDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDate joinedAt;
    private List<CourseSummaryDTO> publishedCourses;
    private Double averageRating;

    public InstructorProfileDTO() {}

    public InstructorProfileDTO(Long id, String name, String email, LocalDate joinedAt, List<CourseSummaryDTO> publishedCourses, Double averageRating) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.joinedAt = joinedAt;
        this.publishedCourses = publishedCourses;
        this.averageRating = averageRating;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public LocalDate getJoinedAt() { return joinedAt; }
    public List<CourseSummaryDTO> getPublishedCourses() { return publishedCourses; }
    public Double getAverageRating() { return averageRating; }
}
