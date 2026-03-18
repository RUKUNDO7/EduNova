package com.elearning.platform.service;

import com.elearning.platform.domain.CommunicationThread;
import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Message;
import com.elearning.platform.domain.UserAccount;
import com.elearning.platform.dto.MessageRequest;
import com.elearning.platform.dto.ThreadRequest;
import com.elearning.platform.repository.CommunicationThreadRepository;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.repository.MessageRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunicationService {

    private final CommunicationThreadRepository threadRepository;
    private final MessageRepository messageRepository;
    private final CourseRepository courseRepository;
    private final com.elearning.platform.repository.EnrollmentRepository enrollmentRepository;
    private final UserService userService;

    public CommunicationService(CommunicationThreadRepository threadRepository,
                                MessageRepository messageRepository,
                                CourseRepository courseRepository,
                                com.elearning.platform.repository.EnrollmentRepository enrollmentRepository,
                                UserService userService) {
        this.threadRepository = threadRepository;
        this.messageRepository = messageRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userService = userService;
    }

    @Transactional
    public CommunicationThread createThread(ThreadRequest request, Long actorId) {
        UserAccount actor = userService.findOne(actorId);
        
        if (request.getCourseId() != null) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new IllegalArgumentException("Course not found"));
            
            // Security check: Only enrolled students or the course instructor/admin can create threads
            if (actor.getRole() != com.elearning.platform.domain.UserRole.ADMIN) {
                if (actor.getRole() == com.elearning.platform.domain.UserRole.STUDENT) {
                    if (!enrollmentRepository.existsByLearnerIdAndCourseId(actorId, request.getCourseId())) {
                        throw new IllegalStateException("Students can only create threads for courses they are enrolled in");
                    }
                } else if (actor.getRole() == com.elearning.platform.domain.UserRole.INSTRUCTOR) {
                    if (course.getInstructor() == null || !course.getInstructor().getId().equals(actorId)) {
                        throw new IllegalStateException("Instructors can only create threads for their own courses");
                    }
                }
            }
            
            CommunicationThread thread = new CommunicationThread(request.getTopic(), actor);
            thread.setCourse(course);
            return threadRepository.save(thread);
        }
        
        return threadRepository.save(new CommunicationThread(request.getTopic(), actor));
    }

    @Transactional
    public Message post(Long threadId, MessageRequest request, Long actorId) {
        UserAccount actor = userService.findOne(actorId);
        CommunicationThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found"));
        
        // Security check: If thread is linked to a course, only participants can post
        if (thread.getCourse() != null && actor.getRole() != com.elearning.platform.domain.UserRole.ADMIN) {
            Long courseId = thread.getCourse().getId();
            if (actor.getRole() == com.elearning.platform.domain.UserRole.STUDENT) {
                if (!enrollmentRepository.existsByLearnerIdAndCourseId(actorId, courseId)) {
                    throw new IllegalStateException("Students can only post in threads for courses they are enrolled in");
                }
            } else if (actor.getRole() == com.elearning.platform.domain.UserRole.INSTRUCTOR) {
                if (thread.getCourse().getInstructor() == null || !thread.getCourse().getInstructor().getId().equals(actorId)) {
                    throw new IllegalStateException("Instructors can only post in threads for their own courses");
                }
            }
        }
        
        Message message = new Message(request.getContent(), actor);
        message.setThread(thread);
        return messageRepository.save(message);
    }

    public List<Message> fetchMessages(Long threadId) {
        return messageRepository.findByThreadId(threadId);
    }

    public List<CommunicationThread> listThreads(Long courseId) {
        if (courseId == null) {
            return threadRepository.findAll();
        }
        return threadRepository.findByCourseId(courseId);
    }
}
