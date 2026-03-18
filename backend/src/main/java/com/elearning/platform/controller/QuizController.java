package com.elearning.platform.controller;

import com.elearning.platform.dto.*;
import com.elearning.platform.security.UserDetailsImpl;
import com.elearning.platform.service.QuizService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/course/{courseId}")
    public List<QuizDTO> list(@PathVariable Long courseId) {
        return quizService.listForCourse(courseId);
    }

    @GetMapping("/{quizId}")
    public QuizDetailDTO detail(@PathVariable Long quizId) {
        return quizService.getQuizDetail(quizId);
    }

    @PostMapping("/course/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public QuizDTO create(@PathVariable Long courseId,
                          @AuthenticationPrincipal UserDetailsImpl userDetails,
                          @Valid @RequestBody QuizRequest request) {
        return quizService.create(courseId, request, userDetails.getId());
    }

    @PostMapping("/{quizId}/questions")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public QuestionDTO addQuestion(@PathVariable Long quizId,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @Valid @RequestBody QuestionRequest request) {
        return quizService.addQuestion(quizId, request, userDetails.getId());
    }

    @PostMapping("/{quizId}/attempts")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STUDENT')")
    public QuizAttemptDTO attempt(@PathVariable Long quizId,
                                  @AuthenticationPrincipal UserDetailsImpl userDetails,
                                  @Valid @RequestBody QuizAttemptRequest request) {
        return quizService.submitAttempt(quizId, request, userDetails.getId());
    }

    @GetMapping("/learner/{learnerId}/attempts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or (hasRole('STUDENT') and #learnerId == principal.id)")
    public List<QuizAttemptDTO> attemptsForLearner(@PathVariable Long learnerId) {
        return quizService.listAttemptsForLearner(learnerId);
    }

    @GetMapping("/{quizId}/attempts/history")
    @PreAuthorize("hasRole('STUDENT')")
    public List<QuizAttemptDTO> getAttemptHistory(@PathVariable Long quizId,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return quizService.getAttemptHistory(quizId, userDetails.getId());
    }

    @GetMapping("/{quizId}/attempts/best-score")
    @PreAuthorize("hasRole('STUDENT')")
    public Integer getBestScore(@PathVariable Long quizId,
                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return quizService.getBestScore(quizId, userDetails.getId());
    }
}
