package com.elearning.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class QuestionRequest {

    @NotBlank
    private String questionText;

    @NotNull
    private Integer position;

    @NotNull
    private List<AnswerOptionRequest> answers;

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public List<AnswerOptionRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerOptionRequest> answers) {
        this.answers = answers;
    }
}
