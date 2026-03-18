package com.elearning.platform.dto;

import java.time.LocalDateTime;

public class QuizDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private int questionCount;

    public QuizDTO(Long id, String title, String description, LocalDateTime createdAt, int questionCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.questionCount = questionCount;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getQuestionCount() {
        return questionCount;
    }
}
