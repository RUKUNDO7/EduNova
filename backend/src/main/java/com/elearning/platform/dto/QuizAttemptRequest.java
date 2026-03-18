package com.elearning.platform.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class QuizAttemptRequest {

    @NotNull
    private List<QuizAnswerRequest> answers;

    public List<QuizAnswerRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuizAnswerRequest> answers) {
        this.answers = answers;
    }
}
