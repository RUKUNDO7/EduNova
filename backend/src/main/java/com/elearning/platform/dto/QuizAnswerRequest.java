package com.elearning.platform.dto;

import jakarta.validation.constraints.NotNull;

public class QuizAnswerRequest {

    @NotNull
    private Long questionId;

    @NotNull
    private Long answerId;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }
}
