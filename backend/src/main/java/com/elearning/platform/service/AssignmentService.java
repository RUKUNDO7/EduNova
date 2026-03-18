package com.elearning.platform.service;

import com.elearning.platform.domain.Assignment;
import com.elearning.platform.domain.UserAccount;
import com.elearning.platform.domain.Submission;
import com.elearning.platform.dto.AssignmentDTO;
import com.elearning.platform.dto.AssignmentRequest;
import com.elearning.platform.dto.SubmissionDTO;
import com.elearning.platform.dto.SubmissionGradeRequest;
import com.elearning.platform.dto.SubmissionRequest;
import com.elearning.platform.dto.UserSummaryDTO;
import com.elearning.platform.repository.AssignmentRepository;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.repository.SubmissionRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final UserService userService;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             SubmissionRepository submissionRepository,
                             CourseRepository courseRepository,
                             UserService userService) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<AssignmentDTO> listForCourse(Long courseId) {
        return assignmentRepository.findByCourseId(courseId).stream()
                .map(this::toAssignmentDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AssignmentDTO> listOverdue(Long courseId) {
        List<AssignmentDTO> result;
        if (courseId != null) {
            result = assignmentRepository.findByDueDateBeforeAndCourseId(LocalDate.now(), courseId)
                    .stream().map(this::toAssignmentDTO).toList();
        } else {
            result = assignmentRepository.findByDueDateBefore(LocalDate.now())
                    .stream().map(this::toAssignmentDTO).toList();
        }
        return result;
    }

    @Transactional
    public AssignmentDTO create(Long courseId, AssignmentRequest request, Long actorId) {
        var course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        var instructor = userService.findOne(actorId);
        var assignment = new Assignment(request.getTitle(), request.getDescription(), request.getDueDate(), request.getMaxScore());
        assignment.setCourse(course);
        assignment.setInstructor(instructor);
        return toAssignmentDTO(assignmentRepository.save(assignment));
    }

    @Transactional
    public SubmissionDTO submit(Long assignmentId, SubmissionRequest request, Long learnerId) {
        var assignment = assignmentRepository.findById(assignmentId).orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        var learner = userService.findOne(learnerId);
        var submission = new Submission(assignment, learner, request.getFileUrl());
        submission.setStatus("SUBMITTED");
        return toSubmissionDTO(submissionRepository.save(submission));
    }

    @Transactional
    public SubmissionDTO gradeSubmission(Long submissionId, SubmissionGradeRequest request, Long actorId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
        UserAccount actor = userService.findOne(actorId);

        // Security check: Only Admin or the Course Instructor can grade
        if (actor.getRole() != com.elearning.platform.domain.UserRole.ADMIN) {
            if (!submission.getAssignment().getCourse().getInstructor().getId().equals(actorId)) {
                throw new IllegalStateException("Instructors can only grade submissions for their own courses");
            }
        }

        submission.setGrade(request.getGrade());
        submission.setFeedback(request.getFeedback());
        submission.setStatus("GRADED");
        return toSubmissionDTO(submissionRepository.save(submission));
    }

    @Transactional(readOnly = true)
    public List<SubmissionDTO> findByLearner(Long learnerId) {
        return submissionRepository.findByLearnerId(learnerId).stream()
                .map(this::toSubmissionDTO)
                .toList();
    }

    private AssignmentDTO toAssignmentDTO(Assignment assignment) {
        UserAccount instructor = assignment.getInstructor();
        UserSummaryDTO instructorDTO = instructor == null
                ? null
                : new UserSummaryDTO(instructor.getId(), instructor.getName(), instructor.getRole());
        return new AssignmentDTO(
                assignment.getId(),
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDueDate(),
                assignment.getMaxScore(),
                assignment.getCreatedAt(),
                instructorDTO
        );
    }

    private SubmissionDTO toSubmissionDTO(Submission submission) {
        UserAccount learner = submission.getLearner();
        return new SubmissionDTO(
                submission.getId(),
                submission.getAssignment().getId(),
                learner.getId(),
                learner.getName(),
                submission.getFileUrl(),
                submission.getGrade(),
                submission.getFeedback(),
                submission.getStatus(),
                submission.getSubmittedAt()
        );
    }
}

