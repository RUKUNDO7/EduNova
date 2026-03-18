package com.elearning.platform.repository;

import com.elearning.platform.domain.QuizAttempt;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByLearnerId(Long learnerId);
    List<QuizAttempt> findByQuizId(Long quizId);
    List<QuizAttempt> findByLearnerIdAndQuizId(Long learnerId, Long quizId);
}
