package com.elearning.platform.controller;

import com.elearning.platform.dto.AnnouncementCreateRequest;
import com.elearning.platform.dto.AnnouncementDTO;
import com.elearning.platform.security.UserDetailsImpl;
import com.elearning.platform.service.AnnouncementService;
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
@RequestMapping("/api/courses/{id}/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    public List<AnnouncementDTO> list(@PathVariable Long id) {
        return announcementService.getForCourse(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public AnnouncementDTO post(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetailsImpl userDetails,
                                @Valid @RequestBody AnnouncementCreateRequest request) {
        return announcementService.post(id, userDetails.getId(), request);
    }
}
