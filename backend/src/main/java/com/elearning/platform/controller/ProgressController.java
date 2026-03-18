package com.elearning.platform.controller;

import com.elearning.platform.domain.ProgressRecord;
import com.elearning.platform.dto.ProgressReportDTO;
import com.elearning.platform.dto.ProgressRequest;
import com.elearning.platform.dto.StudentStatsDTO;
import com.elearning.platform.security.UserDetailsImpl;
import com.elearning.platform.service.ProgressService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/lessons/{lessonId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    public ProgressRecord track(@PathVariable Long lessonId,
                                @AuthenticationPrincipal UserDetailsImpl userDetails,
                                @Valid @RequestBody ProgressRequest request) {
        return progressService.record(lessonId, userDetails.getId(), request);
    }

    @GetMapping("/learner/{learnerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INSTRUCTOR') or (hasRole('STUDENT') and #learnerId == principal.id)")
    public ProgressReportDTO report(@PathVariable Long learnerId) {
        return progressService.reportByLearner(learnerId);
    }

    @GetMapping("/stats")
    public StudentStatsDTO getStats(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return progressService.getStudentStats(userDetails.getId());
    }
}
