package com.elearning.platform.dto;

import java.util.List;

public class QuestionDTO {

    private Long id;
    private String questionText;
    private Integer position;
    private List<AnswerDTO> answers;

    public QuestionDTO(Long id, String questionText, Integer position, List<AnswerDTO> answers) {
        this.id = id;
        this.questionText = questionText;
        this.position = position;
        this.answers = answers;
    }

    public Long getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public Integer getPosition() {
        return position;
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }
}
