package com.elearning.platform.controller;

import com.elearning.platform.dto.InstructorProfileDTO;
import com.elearning.platform.service.CourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    private final CourseService courseService;

    public InstructorController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{id}/profile")
    public InstructorProfileDTO getProfile(@PathVariable Long id) {
        return courseService.getInstructorProfile(id);
    }
}
