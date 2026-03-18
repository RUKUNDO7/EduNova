package com.elearning.platform.service;

import com.elearning.platform.domain.Lesson;
import com.elearning.platform.domain.ProgressRecord;
import com.elearning.platform.domain.UserAccount;
import com.elearning.platform.dto.ProgressReportDTO;
import com.elearning.platform.dto.ProgressRequest;
import com.elearning.platform.dto.StudentStatsDTO;
import com.elearning.platform.repository.EnrollmentRepository;
import com.elearning.platform.repository.LessonRepository;
import com.elearning.platform.repository.ProgressRecordRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgressService {

    private final ProgressRecordRepository progressRecordRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final UserService userService;

    public ProgressService(ProgressRecordRepository progressRecordRepository,
                           EnrollmentRepository enrollmentRepository,
                           LessonRepository lessonRepository,
                           UserService userService) {
        this.progressRecordRepository = progressRecordRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonRepository = lessonRepository;
        this.userService = userService;
    }

    @Transactional
    public ProgressRecord record(Long lessonId, Long learnerId, ProgressRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new IllegalArgumentException("Lesson not found"));
        UserAccount learner = userService.findOne(learnerId);

        // Security check: Only enrolled students can record progress
        if (!enrollmentRepository.existsByLearnerIdAndCourseId(learnerId, lesson.getCourse().getId())) {
            throw new IllegalStateException("You must be enrolled in the course to record progress for its lessons");
        }

        boolean completed = request.getCompleted() == null || request.getCompleted();
        ProgressRecord record = new ProgressRecord(learner, lesson, request.getTimeSpentMinutes(), request.getQuizScore(), completed);
        return progressRecordRepository.save(record);
    }

    public ProgressReportDTO reportByLearner(Long learnerId) {
        List<ProgressRecord> records = progressRecordRepository.findByLearnerId(learnerId);
        UserAccount learner = userService.findOne(learnerId);
        Map<String, Integer> summary = records.stream()
                .collect(Collectors.toMap(r -> r.getLesson().getTitle(), ProgressRecord::getQuizScore, Integer::max));
        long completedCount = records.stream().filter(ProgressRecord::isCompleted).count();
        return new ProgressReportDTO(learner.getId(), learner.getName(), (int) completedCount, summary);
    }

    @Transactional(readOnly = true)
    public StudentStatsDTO getStudentStats(Long studentId) {
        UserAccount learner = userService.findOne(studentId);
        int totalEnrolled = enrollmentRepository.findByLearnerId(studentId).size();
        
        List<ProgressRecord> records = progressRecordRepository.findByLearnerId(studentId);
        
        // Count how many courses have all lessons completed
        Map<Long, Long> completedLessonsByCourse = records.stream()
                .filter(ProgressRecord::isCompleted)
                .collect(Collectors.groupingBy(r -> r.getLesson().getCourse().getId(), Collectors.counting()));
        
        long totalCompleted = completedLessonsByCourse.entrySet().stream()
                .filter(entry -> entry.getValue() >= lessonRepository.countByCourseId(entry.getKey()))
                .count();

        double avgQuizScore = records.stream()
                .filter(r -> r.getQuizScore() != null)
                .mapToInt(ProgressRecord::getQuizScore)
                .average()
                .orElse(0.0);

        return new StudentStatsDTO(learner.getId(), learner.getName(), totalEnrolled, (int) totalCompleted, avgQuizScore);
    }
}
