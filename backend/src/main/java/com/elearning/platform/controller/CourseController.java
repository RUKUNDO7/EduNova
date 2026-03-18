package com.elearning.platform.controller;

import com.elearning.platform.domain.Lesson;
import com.elearning.platform.domain.Level;
import com.elearning.platform.dto.CourseCreateRequest;
import com.elearning.platform.dto.CourseDetailDTO;
import com.elearning.platform.dto.CoursePublishRequest;
import com.elearning.platform.dto.CourseSummaryDTO;
import com.elearning.platform.dto.CourseUpdateRequest;
import com.elearning.platform.dto.LessonDTO;
import com.elearning.platform.dto.LessonRequest;
import com.elearning.platform.security.UserDetailsImpl;
import com.elearning.platform.service.CourseService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseSummaryDTO> listCourses(@RequestParam(required = false) Level level,
                                               @RequestParam(required = false) String query,
                                               @RequestParam(required = false) String tag,
                                               @RequestParam(defaultValue = "false") boolean includeDrafts) {
        if (query != null && !query.isBlank()) {
            return courseService.searchCourses(query);
        }
        if (tag != null && !tag.isBlank()) {
            return courseService.searchCoursesByTag(tag);
        }
        return courseService.listCourses(level, includeDrafts);
    }

    @GetMapping("/{id}")
    public CourseDetailDTO getCourse(@PathVariable Long id) {
        return courseService.getCourseDetail(id);
    }

    @GetMapping("/featured")
    public List<CourseSummaryDTO> getFeatured() {
        return courseService.getFeaturedCourses();
    }

    @GetMapping("/{id}/recommendations")
    public List<CourseSummaryDTO> getRecommendations(@PathVariable Long id) {
        return courseService.getRecommendedCourses(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public CourseSummaryDTO create(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @Valid @RequestBody CourseCreateRequest request) {
        return courseService.createCourse(request, userDetails.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public CourseSummaryDTO update(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @Valid @RequestBody CourseUpdateRequest request) {
        return courseService.updateCourse(id, request, userDetails.getId());
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public CourseSummaryDTO publish(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @Valid @RequestBody CoursePublishRequest request) {
        return courseService.publishCourse(id, request, userDetails.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        courseService.deleteCourse(id, userDetails.getId());
    }

    @PostMapping("/{id}/lessons")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public LessonDTO addLesson(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetailsImpl userDetails,
                               @Valid @RequestBody LessonRequest request) {
        Lesson lesson = new Lesson(request.getTitle(), request.getSummary(), request.getContentUrl(), request.getSequenceNumber(), request.getDurationMinutes());
        Lesson saved = courseService.addLesson(id, lesson, userDetails.getId());
        return new LessonDTO(saved.getId(), saved.getTitle(), saved.getSummary(), saved.getContentUrl(), saved.getSequenceNumber(), saved.getDurationMinutes());
    }
}
