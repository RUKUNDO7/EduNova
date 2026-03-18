package com.elearning.platform.service;

import com.elearning.platform.domain.Answer;
import com.elearning.platform.domain.Question;
import com.elearning.platform.domain.Quiz;
import com.elearning.platform.domain.QuizAttempt;
import com.elearning.platform.domain.UserAccount;
import com.elearning.platform.domain.UserRole;
import com.elearning.platform.dto.AnswerDTO;
import com.elearning.platform.dto.AnswerOptionRequest;
import com.elearning.platform.dto.QuestionDTO;
import com.elearning.platform.dto.QuestionRequest;
import com.elearning.platform.dto.QuizAnswerRequest;
import com.elearning.platform.dto.QuizAttemptDTO;
import com.elearning.platform.dto.QuizAttemptRequest;
import com.elearning.platform.dto.QuizDTO;
import com.elearning.platform.dto.QuizDetailDTO;
import com.elearning.platform.dto.QuizRequest;
import com.elearning.platform.repository.AnswerRepository;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.repository.QuestionRepository;
import com.elearning.platform.repository.QuizAttemptRepository;
import com.elearning.platform.repository.QuizRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final CourseRepository courseRepository;
    private final UserService userService;

    public QuizService(QuizRepository quizRepository,
                       QuestionRepository questionRepository,
                       AnswerRepository answerRepository,
                       QuizAttemptRepository quizAttemptRepository,
                       CourseRepository courseRepository,
                       UserService userService) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    public List<QuizDTO> listForCourse(Long courseId) {
        return quizRepository.findByCourseId(courseId).stream()
                .map(this::toQuizDTO)
                .toList();
    }

    public QuizDetailDTO getQuizDetail(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
        List<QuestionDTO> questions = questionRepository.findByQuizId(quizId).stream()
                .sorted((a, b) -> Integer.compare(a.getPosition(), b.getPosition()))
                .map(this::toQuestionDTO)
                .toList();
        return new QuizDetailDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getCreatedAt(), questions);
    }

    @Transactional
    public QuizDTO create(Long courseId, QuizRequest request, Long actorId) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        UserAccount actor = userService.findOne(actorId);

        // Security check: Only Admin or the Course Instructor can create quizzes
        if (actor.getRole() != UserRole.ADMIN) {
            if (course.getInstructor() == null || !course.getInstructor().getId().equals(actorId)) {
                throw new IllegalStateException("Instructors can only create quizzes for their own courses");
            }
        }

        Quiz quiz = new Quiz(request.getTitle(), request.getDescription());
        quiz.setCourse(course);
        return toQuizDTO(quizRepository.save(quiz));
    }

    @Transactional
    public QuestionDTO addQuestion(Long quizId, QuestionRequest request, Long actorId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
        UserAccount actor = userService.findOne(actorId);

        // Security check: Only Admin or the Course Instructor can add questions
        if (actor.getRole() != UserRole.ADMIN) {
            if (quiz.getCourse().getInstructor() == null || !quiz.getCourse().getInstructor().getId().equals(actorId)) {
                throw new IllegalStateException("Instructors can only add questions to quizzes for their own courses");
            }
        }

        Question question = new Question(request.getQuestionText(), request.getPosition());
        question.setQuiz(quiz);
        Question savedQuestion = questionRepository.save(question);
        for (AnswerOptionRequest option : request.getAnswers()) {
            Answer answer = new Answer(option.getAnswerText(), option.isCorrect());
            answer.setQuestion(savedQuestion);
            answerRepository.save(answer);
        }
        List<AnswerDTO> answers = answerRepository.findByQuestionId(savedQuestion.getId()).stream()
                .map(this::toAnswerDTO)
                .toList();
        return new QuestionDTO(savedQuestion.getId(), savedQuestion.getQuestionText(), savedQuestion.getPosition(), answers);
    }

    @Transactional
    public QuizAttemptDTO submitAttempt(Long quizId, QuizAttemptRequest request, Long learnerId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
        UserAccount learner = userService.findOne(learnerId);

        List<Question> questions = questionRepository.findByQuizId(quizId);
        Map<Long, Long> correctAnswerByQuestion = new HashMap<>();
        for (Question question : questions) {
            List<Answer> answers = answerRepository.findByQuestionId(question.getId());
            answers.stream().filter(Answer::isCorrect).findFirst()
                    .ifPresent(answer -> correctAnswerByQuestion.put(question.getId(), answer.getId()));
        }

        Map<Long, Long> submittedByQuestion = request.getAnswers().stream()
                .collect(Collectors.toMap(QuizAnswerRequest::getQuestionId, QuizAnswerRequest::getAnswerId, (a, b) -> a));

        int correct = 0;
        for (Map.Entry<Long, Long> entry : correctAnswerByQuestion.entrySet()) {
            Long submitted = submittedByQuestion.get(entry.getKey());
            if (submitted != null && submitted.equals(entry.getValue())) {
                correct++;
            }
        }

        int total = correctAnswerByQuestion.size();
        int score = total == 0 ? 0 : Math.round((correct * 100.0f) / total);
        QuizAttempt attempt = new QuizAttempt(quiz, learner, score, correct, total);
        QuizAttempt saved = quizAttemptRepository.save(attempt);

        return new QuizAttemptDTO(saved.getId(), quizId, learnerId, saved.getScore(), saved.getCorrectCount(), saved.getTotalQuestions(), saved.getSubmittedAt());
    }

    public List<QuizAttemptDTO> listAttemptsForLearner(Long learnerId) {
        return quizAttemptRepository.findByLearnerId(learnerId).stream()
                .map(attempt -> new QuizAttemptDTO(attempt.getId(), attempt.getQuiz().getId(), attempt.getLearner().getId(),
                        attempt.getScore(), attempt.getCorrectCount(), attempt.getTotalQuestions(), attempt.getSubmittedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<QuizAttemptDTO> getAttemptHistory(Long quizId, Long learnerId) {
        return quizAttemptRepository.findByLearnerIdAndQuizId(learnerId, quizId).stream()
                .map(attempt -> new QuizAttemptDTO(attempt.getId(), attempt.getQuiz().getId(), attempt.getLearner().getId(),
                        attempt.getScore(), attempt.getCorrectCount(), attempt.getTotalQuestions(), attempt.getSubmittedAt()))
                .sorted(java.util.Comparator.comparing(QuizAttemptDTO::getSubmittedAt).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public Integer getBestScore(Long quizId, Long learnerId) {
        return quizAttemptRepository.findByLearnerIdAndQuizId(learnerId, quizId).stream()
                .map(QuizAttempt::getScore)
                .max(Integer::compareTo)
                .orElse(0);
    }

    private QuizDTO toQuizDTO(Quiz quiz) {
        int questionCount = questionRepository.findByQuizId(quiz.getId()).size();
        return new QuizDTO(quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getCreatedAt(), questionCount);
    }

    private QuestionDTO toQuestionDTO(Question question) {
        List<AnswerDTO> answers = answerRepository.findByQuestionId(question.getId()).stream()
                .map(this::toAnswerDTO)
                .toList();
        return new QuestionDTO(question.getId(), question.getQuestionText(), question.getPosition(), answers);
    }

    private AnswerDTO toAnswerDTO(Answer answer) {
        return new AnswerDTO(answer.getId(), answer.getAnswerText(), answer.isCorrect());
    }
}
