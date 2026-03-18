package com.elearning.platform.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private UserAccount learner;

    private Integer score;

    private Integer correctCount;

    private Integer totalQuestions;

    private LocalDateTime submittedAt = LocalDateTime.now();

    public QuizAttempt() {
    }

    public QuizAttempt(Quiz quiz, UserAccount learner, Integer score, Integer correctCount, Integer totalQuestions) {
        this.quiz = quiz;
        this.learner = learner;
        this.score = score;
        this.correctCount = correctCount;
        this.totalQuestions = totalQuestions;
    }

    public Long getId() {
        return id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public UserAccount getLearner() {
        return learner;
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
