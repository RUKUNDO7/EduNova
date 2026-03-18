package com.elearning.platform.dto;

import jakarta.validation.constraints.NotBlank;

public class AnswerOptionRequest {

    @NotBlank
    private String answerText;

    private boolean correct;

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
