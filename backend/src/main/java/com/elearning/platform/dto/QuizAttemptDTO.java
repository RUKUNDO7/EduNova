package com.elearning.platform.dto;

import java.time.LocalDateTime;

public class QuizAttemptDTO {

    private Long id;
    private Long quizId;
    private Long learnerId;
    private Integer score;
    private Integer correctCount;
    private Integer totalQuestions;
    private LocalDateTime submittedAt;

    public QuizAttemptDTO(Long id, Long quizId, Long learnerId, Integer score, Integer correctCount, Integer totalQuestions, LocalDateTime submittedAt) {
        this.id = id;
        this.quizId = quizId;
        this.learnerId = learnerId;
        this.score = score;
        this.correctCount = correctCount;
        this.totalQuestions = totalQuestions;
        this.submittedAt = submittedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getQuizId() {
        return quizId;
    }

    public Long getLearnerId() {
        return learnerId;
    }

    public Integer getScore() {
        return score;
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
}
