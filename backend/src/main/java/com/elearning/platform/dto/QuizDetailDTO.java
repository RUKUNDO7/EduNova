package com.elearning.platform.dto;

import java.time.LocalDateTime;
import java.util.List;

public class QuizDetailDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private List<QuestionDTO> questions;

    public QuizDetailDTO(Long id, String title, String description, LocalDateTime createdAt, List<QuestionDTO> questions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.questions = questions;
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

    public List<QuestionDTO> getQuestions() {
        return questions;
    }
}
