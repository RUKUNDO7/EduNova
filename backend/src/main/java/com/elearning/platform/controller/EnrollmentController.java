package com.elearning.platform.controller;

import com.elearning.platform.domain.Enrollment;
import com.elearning.platform.dto.EnrollmentRequest;
import com.elearning.platform.service.EnrollmentService;
import jakarta.validation.Valid;
import java.util.List;
import com.elearning.platform.security.UserDetailsImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public Enrollment enroll(@AuthenticationPrincipal UserDetailsImpl userDetails,
                             @Valid @RequestBody EnrollmentRequest request) {
        // We override the learner details from the authenticated user for security
        request.setLearnerEmail(userDetails.getUsername());
        request.setLearnerName(userDetails.getUsername()); // Or full name if we had it
        return enrollmentService.enroll(request);
    }

    @GetMapping("/learner/{learnerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or (hasRole('STUDENT') and #learnerId == principal.id)")
    public List<Enrollment> findByLearner(@PathVariable Long learnerId) {
        return enrollmentService.findByLearner(learnerId);
    }
}
