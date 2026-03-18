package com.elearning.platform.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AssignmentDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer maxScore;
    private LocalDateTime createdAt;
    private UserSummaryDTO instructor;

    public AssignmentDTO(Long id, String title, String description, LocalDate dueDate, Integer maxScore, LocalDateTime createdAt, UserSummaryDTO instructor) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.maxScore = maxScore;
        this.createdAt = createdAt;
        this.instructor = instructor;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public UserSummaryDTO getInstructor() {
        return instructor;
    }
}
