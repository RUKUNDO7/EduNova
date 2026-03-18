package com.elearning.platform.dto;

public class AnswerDTO {

    private Long id;
    private String answerText;
    private boolean correct;

    public AnswerDTO(Long id, String answerText, boolean correct) {
        this.id = id;
        this.answerText = answerText;
        this.correct = correct;
    }

    public Long getId() {
        return id;
    }

    public String getAnswerText() {
        return answerText;
    }

    public boolean isCorrect() {
        return correct;
    }
}
