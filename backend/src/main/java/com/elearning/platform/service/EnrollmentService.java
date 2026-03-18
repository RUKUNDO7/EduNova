package com.elearning.platform.service;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Enrollment;
import com.elearning.platform.domain.UserAccount;
import com.elearning.platform.domain.ProgressRecord;
import com.elearning.platform.dto.EnrollmentRequest;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.repository.EnrollmentRepository;
import com.elearning.platform.repository.LessonRepository;
import com.elearning.platform.repository.ProgressRecordRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final ProgressRecordRepository progressRecordRepository;
    private final UserService userService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             LessonRepository lessonRepository,
                             ProgressRecordRepository progressRecordRepository,
                             UserService userService) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.progressRecordRepository = progressRecordRepository;
        this.userService = userService;
    }

    @Transactional
    public Enrollment enroll(EnrollmentRequest request) {
        UserAccount learner = userService.createOrGetStudent(request.getLearnerName(), request.getLearnerEmail());
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (course.getPrerequisiteId() != null) {
            long totalPrereqLessons = lessonRepository.countByCourseId(course.getPrerequisiteId());
            long completedPrereqLessons = progressRecordRepository.findByLearnerIdAndLessonCourseId(learner.getId(), course.getPrerequisiteId())
                    .stream().filter(ProgressRecord::isCompleted).count();
            if (completedPrereqLessons < totalPrereqLessons || totalPrereqLessons == 0) {
                 // Note: If totalPrereqLessons is 0, it means the course has no lessons, 
                 // but we'll still block it if it's set as a prerequisite unless it's a dummy value.
                 // For now, let's assume valid prerequisite courses have lessons.
                throw new IllegalStateException("Prerequisite course not completed");
            }
        }

        Enrollment enrollment = new Enrollment(course, learner);
        enrollment.setStatus("ACTIVE");
        return enrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findByLearner(Long learnerId) {
        return enrollmentRepository.findByLearnerId(learnerId);
    }
}
