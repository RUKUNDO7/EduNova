package com.elearning.platform.service;

import com.elearning.platform.domain.Category;
import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Lesson;
import com.elearning.platform.domain.Level;
import com.elearning.platform.domain.Material;
import com.elearning.platform.domain.UserAccount;
import com.elearning.platform.domain.UserRole;
import com.elearning.platform.dto.CategoryDTO;
import com.elearning.platform.dto.CourseCreateRequest;
import com.elearning.platform.dto.CourseDetailDTO;
import com.elearning.platform.dto.CoursePublishRequest;
import com.elearning.platform.dto.CourseSummaryDTO;
import com.elearning.platform.dto.CourseUpdateRequest;
import com.elearning.platform.dto.InstructorProfileDTO;
import com.elearning.platform.dto.LessonDTO;
import com.elearning.platform.dto.MaterialDTO;
import com.elearning.platform.dto.UserSummaryDTO;
import com.elearning.platform.repository.CategoryRepository;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.repository.LessonRepository;
import com.elearning.platform.repository.MaterialRepository;
import com.elearning.platform.repository.ReviewRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final MaterialRepository materialRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    public CourseService(CourseRepository courseRepository,
                         LessonRepository lessonRepository,
                         MaterialRepository materialRepository,
                         CategoryRepository categoryRepository,
                         ReviewRepository reviewRepository,
                         UserService userService) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.materialRepository = materialRepository;
        this.categoryRepository = categoryRepository;
        this.reviewRepository = reviewRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<CourseSummaryDTO> listCourses(Level level, boolean includeDrafts) {
        List<Course> courses;
        if (includeDrafts) {
            courses = courseRepository.findAll();
        } else {
            courses = courseRepository.findByPublishedTrue();
        }

        if (level != null) {
            courses = courses.stream().filter(c -> c.getLevel() == level).toList();
        }

        return courses.stream()
                .sorted(Comparator.comparing(Course::getCreatedAt).reversed())
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseSummaryDTO> searchCoursesByTag(String tag) {
        return courseRepository.searchCoursesByTag(tag).stream()
                .sorted(Comparator.comparing(Course::getCreatedAt).reversed())
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InstructorProfileDTO getInstructorProfile(Long instructorId) {
        UserAccount instructor = userService.findOne(instructorId);
        if (instructor.getRole() != UserRole.INSTRUCTOR && instructor.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("User is not an instructor");
        }

        List<Course> courses = courseRepository.findByInstructorIdAndPublishedTrue(instructorId);
        List<CourseSummaryDTO> publishedCourses = courses.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());

        double averageRating = courses.stream()
                .map(c -> {
                    var reviews = reviewRepository.findByCourseId(c.getId());
                    return reviews.isEmpty() ? 0.0 : reviews.stream().mapToInt(r -> r.getRating()).average().orElse(0.0);
                })
                .filter(rating -> rating > 0)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        return new InstructorProfileDTO(
                instructor.getId(),
                instructor.getName(),
                instructor.getEmail(),
                instructor.getJoinedAt().toLocalDate(),
                publishedCourses,
                averageRating
        );
    }

    @Transactional(readOnly = true)
    public List<CourseSummaryDTO> searchCourses(String query) {
        return courseRepository.searchCourses(query).stream()
                .sorted(Comparator.comparing(Course::getCreatedAt).reversed())
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseSummaryDTO> getFeaturedCourses() {
        return courseRepository.findByFeaturedTrueAndPublishedTrue().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseSummaryDTO> getRecommendedCourses(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (course.getCategory() == null) {
            return List.of();
        }
        return courseRepository.findByCategoryIdAndIdNotAndPublishedTrue(course.getCategory().getId(), courseId).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseDetailDTO getCourseDetail(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        List<LessonDTO> lessons = lessonRepository.findByCourseId(courseId).stream()
                .sorted(Comparator.comparing(Lesson::getSequenceNumber))
                .map(this::toLessonDTO)
                .collect(Collectors.toList());
        List<MaterialDTO> materials = materialRepository.findByCourseId(courseId).stream()
                .map(this::toMaterialDTO)
                .collect(Collectors.toList());

        var reviews = reviewRepository.findByCourseId(courseId);
        double avgRating = reviews.stream().mapToInt(r -> r.getRating()).average().orElse(0.0);
        int totalReviews = reviews.size();

        return new CourseDetailDTO(course.getId(), course.getTitle(), course.getDescription(), course.getLevel(), course.getEstimatedHours(), course.isPublished(),
                toCategoryDTO(course.getCategory()), toUserSummary(course.getInstructor()), lessons, materials, avgRating, totalReviews, course.getTags());
    }

    @Transactional
    public CourseSummaryDTO createCourse(CourseCreateRequest request, Long actorId) {
        UserAccount actor = userService.findOne(actorId);

        UserAccount instructor;
        if (actor.getRole() == UserRole.INSTRUCTOR) {
            if (request.getInstructorId() != null && !request.getInstructorId().equals(actorId)) {
                throw new IllegalArgumentException("Instructors can only create courses for themselves");
            }
            instructor = actor;
        } else {
            if (request.getInstructorId() == null) {
                throw new IllegalArgumentException("Instructor is required for course creation");
            }
            instructor = userService.findOne(request.getInstructorId());
            if (instructor.getRole() != UserRole.INSTRUCTOR) {
                throw new IllegalArgumentException("Selected instructor must have INSTRUCTOR role");
            }
        }

        Course course = new Course(request.getTitle(), request.getDescription(), request.getLevel(), request.getEstimatedHours());
        course.setInstructor(instructor);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            course.setCategory(category);
        }

        if (request.getTags() != null) {
            course.getTags().addAll(request.getTags());
        }

        Course saved = courseRepository.save(course);
        return toSummary(saved);
    }

    @Transactional
    public CourseSummaryDTO updateCourse(Long courseId, CourseUpdateRequest request, Long actorId) {
        UserAccount actor = userService.findOne(actorId);

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (actor.getRole() == UserRole.INSTRUCTOR) {
            if (course.getInstructor() == null || !course.getInstructor().getId().equals(actorId)) {
                throw new IllegalArgumentException("Instructors can only update their own courses");
            }
        }

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setLevel(request.getLevel());
        course.setEstimatedHours(request.getEstimatedHours());

        if (actor.getRole() == UserRole.ADMIN && request.getInstructorId() != null) {
            UserAccount instructor = userService.findOne(request.getInstructorId());
            if (instructor.getRole() != UserRole.INSTRUCTOR) {
                throw new IllegalArgumentException("Selected instructor must have INSTRUCTOR role");
            }
            course.setInstructor(instructor);
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            course.setCategory(category);
        }

        if (request.getTags() != null) {
            course.getTags().clear();
            course.getTags().addAll(request.getTags());
        }

        Course saved = courseRepository.save(course);
        return toSummary(saved);
    }

    @Transactional
    public CourseSummaryDTO publishCourse(Long courseId, CoursePublishRequest request, Long actorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        course.setPublished(request.getPublished());
        return toSummary(courseRepository.save(course));
    }

    @Transactional
    public void deleteCourse(Long courseId, Long actorId) {
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("Course not found");
        }
        courseRepository.deleteById(courseId);
    }

    @Transactional
    public Lesson addLesson(Long courseId, Lesson lesson, Long actorId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Course not found"));
        UserAccount actor = userService.findOne(actorId);

        // Security check: Only Admin or the Course Instructor can add lessons
        if (actor.getRole() != UserRole.ADMIN) {
            if (course.getInstructor() == null || !course.getInstructor().getId().equals(actorId)) {
                throw new IllegalStateException("Instructors can only add lessons to their own courses");
            }
        }

        lesson.setCourse(course);
        course.getLessons().add(lesson);
        courseRepository.save(course);
        return lessonRepository.save(lesson);
    }

    private LessonDTO toLessonDTO(Lesson lesson) {
        return new LessonDTO(lesson.getId(), lesson.getTitle(), lesson.getSummary(), lesson.getContentUrl(), lesson.getSequenceNumber(), lesson.getDurationMinutes());
    }

    private MaterialDTO toMaterialDTO(Material material) {
        return new MaterialDTO(material.getId(), material.getTitle(), material.getDescription(), material.getResourceUrl(), material.getType());
    }

    private CourseSummaryDTO toSummary(Course course) {
        return new CourseSummaryDTO(course.getId(), course.getTitle(), course.getLevel(), course.getEstimatedHours(), course.isPublished(),
                toCategoryDTO(course.getCategory()), toUserSummary(course.getInstructor()), course.getTags());
    }

    private CategoryDTO toCategoryDTO(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDTO(category.getId(), category.getName(), category.getDescription());
    }

    private UserSummaryDTO toUserSummary(UserAccount user) {
        if (user == null) {
            return null;
        }
        return new UserSummaryDTO(user.getId(), user.getName(), user.getRole());
    }
}

