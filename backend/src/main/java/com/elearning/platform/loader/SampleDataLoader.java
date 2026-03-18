package com.elearning.platform.loader;

import com.elearning.platform.domain.Answer;
import com.elearning.platform.domain.Assignment;
import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.CommunicationThread;
import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Lesson;
import com.elearning.platform.domain.Level;
import com.elearning.platform.domain.Material;
import com.elearning.platform.domain.MaterialType;
import com.elearning.platform.domain.Message;
import com.elearning.platform.domain.ProgressRecord;
import com.elearning.platform.domain.Question;
import com.elearning.platform.domain.Quiz;
import com.elearning.platform.domain.QuizAttempt;
import com.elearning.platform.domain.Submission;
import com.elearning.platform.domain.UserAccount;
import com.elearning.platform.domain.UserRole;
import com.elearning.platform.repository.AnswerRepository;
import com.elearning.platform.repository.AssignmentRepository;
import com.elearning.platform.repository.CategoryRepository;
import com.elearning.platform.repository.CommunicationThreadRepository;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.repository.MaterialRepository;
import com.elearning.platform.repository.MessageRepository;
import com.elearning.platform.repository.ProgressRecordRepository;
import com.elearning.platform.repository.QuestionRepository;
import com.elearning.platform.repository.QuizAttemptRepository;
import com.elearning.platform.repository.QuizRepository;
import com.elearning.platform.repository.SubmissionRepository;
import com.elearning.platform.repository.UserAccountRepository;
import java.time.LocalDate;
import java.util.Arrays;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SampleDataLoader implements ApplicationRunner {

    private final CourseRepository courseRepository;
    private final UserAccountRepository userAccountRepository;
    private final MaterialRepository materialRepository;
    private final CategoryRepository categoryRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final ProgressRecordRepository progressRecordRepository;
    private final CommunicationThreadRepository threadRepository;
    private final MessageRepository messageRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final PasswordEncoder passwordEncoder;

    public SampleDataLoader(CourseRepository courseRepository,
                            UserAccountRepository userAccountRepository,
                            MaterialRepository materialRepository,
                            CategoryRepository categoryRepository,
                            AssignmentRepository assignmentRepository,
                            SubmissionRepository submissionRepository,
                            ProgressRecordRepository progressRecordRepository,
                            CommunicationThreadRepository threadRepository,
                            MessageRepository messageRepository,
                            QuizRepository quizRepository,
                            QuestionRepository questionRepository,
                            AnswerRepository answerRepository,
                            QuizAttemptRepository quizAttemptRepository,
                            PasswordEncoder passwordEncoder) {
        this.courseRepository = courseRepository;
        this.userAccountRepository = userAccountRepository;
        this.materialRepository = materialRepository;
        this.categoryRepository = categoryRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.progressRecordRepository = progressRecordRepository;
        this.threadRepository = threadRepository;
        this.messageRepository = messageRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (courseRepository.count() > 0) {
            return;
        }

        Category programming = new Category("Programming", "Hands-on development tracks and code labs.");
        Category mathematics = new Category("Mathematics", "Foundational math skills for analytics and engineering.");
        Category design = new Category("Design", "Product design, UX, and creative thinking.");
        categoryRepository.saveAll(Arrays.asList(programming, mathematics, design));

        UserAccount admin = new UserAccount("System Admin", "admin@example.com", passwordEncoder.encode("password"), UserRole.ADMIN);
        UserAccount instructor = new UserAccount("Course Architect", "teacher@example.com", passwordEncoder.encode("password"), UserRole.INSTRUCTOR);
        UserAccount learner = new UserAccount("Student Ada", "student@example.com", passwordEncoder.encode("password"), UserRole.STUDENT);
        userAccountRepository.saveAll(Arrays.asList(admin, instructor, learner));

        Course agile = new Course("Product Thinking for Learners", "A practical guide to building learner-centric products.", Level.INTERMEDIATE, 18);
        agile.setPublished(true);
        agile.setInstructor(instructor);
        agile.setCategory(design);
        Lesson research = new Lesson("Research Routines", "How to keep qualitative discovery continuous.", "https://example.com/research", 1, 25);
        Lesson experiments = new Lesson("Designing Learning Experiments", "Low-cost prototypes to validate habits.", "https://example.com/experiments", 2, 30);
        agile.addLesson(research);
        agile.addLesson(experiments);

        Course security = new Course("Secure Coding Foundations", "Core security controls that every developer should apply.", Level.BEGINNER, 12);
        security.setPublished(true);
        security.setInstructor(instructor);
        security.setCategory(programming);
        security.addLesson(new Lesson("Threat Modeling", "Frame risks before touching code.", "https://example.com/threats", 1, 20));
        security.addLesson(new Lesson("Secrets Management", "Use vaults and short-lived credentials.", "https://example.com/secrets", 2, 15));

        Course dataOps = new Course("DataOps Workflow Essentials", "Pipeline hygiene, observability, and resilience for analysts.", Level.INTERMEDIATE, 22);
        dataOps.setPublished(false);
        dataOps.setInstructor(instructor);
        dataOps.setCategory(mathematics);
        dataOps.addLesson(new Lesson("Idempotent Pipelines", "Avoid cascading failures with guardrails.", "https://example.com/idempotent", 1, 30));
        dataOps.addLesson(new Lesson("Monitoring Data Quality", "Signals you can surface cheaply.", "https://example.com/monitoring", 2, 18));

        courseRepository.saveAll(Arrays.asList(agile, security, dataOps));

        Material material = new Material("Welcome Video", "Orientation video for the agile track", "https://example.com/video", MaterialType.VIDEO);
        material.setCourse(agile);
        material.setUploader(instructor);
        materialRepository.save(material);

        Assignment assignment = new Assignment("Sprint Assignment", "Apply sprint planning to a sample project.", LocalDate.now().plusWeeks(1), 20);
        assignment.setCourse(agile);
        assignment.setInstructor(instructor);
        assignmentRepository.save(assignment);

        Submission submission = new Submission(assignment, learner, "https://example.com/submissions/sprint-plan.pdf");
        submission.setGrade(17);
        submission.setFeedback("Solid reasoning");
        submission.setStatus("GRADED");
        submissionRepository.save(submission);

        Quiz quiz = new Quiz("Sprint Quiz", "Quick check on sprint planning theory");
        quiz.setCourse(agile);
        quizRepository.save(quiz);

        Question question = new Question("How long is a typical sprint?", 1);
        question.setQuiz(quiz);
        questionRepository.save(question);

        Answer correct = new Answer("2-4 weeks", true);
        correct.setQuestion(question);
        answerRepository.save(correct);

        Answer incorrect = new Answer("6-8 weeks", false);
        incorrect.setQuestion(question);
        answerRepository.save(incorrect);

        QuizAttempt attempt = new QuizAttempt(quiz, learner, 100, 1, 1);
        quizAttemptRepository.save(attempt);

        ProgressRecord record = new ProgressRecord(learner, research, 35, 85, true);
        progressRecordRepository.save(record);

        CommunicationThread thread = new CommunicationThread("General Questions", learner);
        thread.setCourse(agile);
        threadRepository.save(thread);
        Message first = new Message("Excited to dig into the materials!", learner);
        first.setThread(thread);
        messageRepository.save(first);
        Message reply = new Message("Welcome! Drop any questions here.", instructor);
        reply.setThread(thread);
        messageRepository.save(reply);
    }
}
