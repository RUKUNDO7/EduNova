package com.elearning.platform.controller;

import com.elearning.platform.dto.AssignmentDTO;
import com.elearning.platform.dto.AssignmentRequest;
import com.elearning.platform.dto.SubmissionDTO;
import com.elearning.platform.dto.SubmissionGradeRequest;
import com.elearning.platform.dto.SubmissionRequest;
import com.elearning.platform.security.UserDetailsImpl;
import com.elearning.platform.service.AssignmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("/course/{courseId}")
    public List<AssignmentDTO> list(@PathVariable Long courseId) {
        return assignmentService.listForCourse(courseId);
    }

    @GetMapping("/overdue")
    public List<AssignmentDTO> listOverdue(@RequestParam(required = false) Long courseId) {
        return assignmentService.listOverdue(courseId);
    }

    @PostMapping("/course/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public AssignmentDTO create(@PathVariable Long courseId,
                                @AuthenticationPrincipal UserDetailsImpl userDetails,
                                @Valid @RequestBody AssignmentRequest request) {
        return assignmentService.create(courseId, request, userDetails.getId());
    }

    @PostMapping("/{assignmentId}/submissions")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STUDENT')")
    public SubmissionDTO submit(@PathVariable Long assignmentId,
                                @AuthenticationPrincipal UserDetailsImpl userDetails,
                                @Valid @RequestBody SubmissionRequest request) {
        return assignmentService.submit(assignmentId, request, userDetails.getId());
    }

    @PatchMapping("/submissions/{submissionId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public SubmissionDTO grade(@PathVariable Long submissionId,
                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                               @Valid @RequestBody SubmissionGradeRequest request) {
        return assignmentService.gradeSubmission(submissionId, request, userDetails.getId());
    }

    @GetMapping("/learner/{learnerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or (hasRole('STUDENT') and #learnerId == principal.id)")
    public List<SubmissionDTO> submissions(@PathVariable Long learnerId) {
        return assignmentService.findByLearner(learnerId);
    }
}
