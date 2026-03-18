package com.elearning.platform.controller;

import com.elearning.platform.domain.CommunicationThread;
import com.elearning.platform.domain.Message;
import com.elearning.platform.dto.MessageRequest;
import com.elearning.platform.dto.ThreadRequest;
import com.elearning.platform.service.CommunicationService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/communications")
public class CommunicationController {

    private final CommunicationService communicationService;

    public CommunicationController(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    @PostMapping("/threads")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public CommunicationThread createThread(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @Valid @RequestBody ThreadRequest request) {
        return communicationService.createThread(request, userDetails.getId());
    }

    @GetMapping("/threads")
    public List<CommunicationThread> listThreads(@RequestParam(required = false) Long courseId) {
        return communicationService.listThreads(courseId);
    }

    @PostMapping("/threads/{threadId}/messages")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public Message postMessage(@PathVariable Long threadId,
                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                               @Valid @RequestBody MessageRequest request) {
        return communicationService.post(threadId, request, userDetails.getId());
    }

    @GetMapping("/threads/{threadId}/messages")
    public List<Message> getMessages(@PathVariable Long threadId) {
        return communicationService.fetchMessages(threadId);
    }
}
