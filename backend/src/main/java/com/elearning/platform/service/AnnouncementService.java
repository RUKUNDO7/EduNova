package com.elearning.platform.service;

import com.elearning.platform.domain.Announcement;
import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.UserAccount;
import com.elearning.platform.dto.AnnouncementCreateRequest;
import com.elearning.platform.dto.AnnouncementDTO;
import com.elearning.platform.repository.AnnouncementRepository;
import com.elearning.platform.repository.CourseRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final CourseRepository courseRepository;
    private final UserService userService;

    public AnnouncementService(AnnouncementRepository announcementRepository,
                               CourseRepository courseRepository,
                               UserService userService) {
        this.announcementRepository = announcementRepository;
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    @Transactional
    public AnnouncementDTO post(Long courseId, Long instructorId, AnnouncementCreateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        UserAccount actor = userService.findOne(instructorId);

        // Security check: Only Admin or the Course Instructor can post announcements
        if (actor.getRole() != com.elearning.platform.domain.UserRole.ADMIN) {
            if (course.getInstructor() == null || !course.getInstructor().getId().equals(instructorId)) {
                throw new IllegalStateException("Instructors can only post announcements for their own courses");
            }
        }

        Announcement saved = announcementRepository.save(
                new Announcement(request.getTitle(), request.getBody(), course, actor));
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getForCourse(Long courseId) {
        return announcementRepository.findByCourseIdOrderByPostedAtDesc(courseId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AnnouncementDTO toDTO(Announcement a) {
        return new AnnouncementDTO(a.getId(), a.getTitle(), a.getBody(),
                a.getInstructor().getName(), a.getPostedAt());
    }
}
