package com.elearning.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Lesson;
import com.elearning.platform.domain.Level;
import com.elearning.platform.repository.CategoryRepository;
import com.elearning.platform.repository.CourseRepository;
import com.elearning.platform.repository.LessonRepository;
import com.elearning.platform.repository.MaterialRepository;
import com.elearning.platform.repository.ReviewRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CourseService courseService;

    private Course published;
    private Course draft;

    @BeforeEach
    void setUp() {
        published = new Course("Published", "desc", Level.BEGINNER, 5);
        published.setPublished(true);
        draft = new Course("Draft", "desc", Level.INTERMEDIATE, 8);
        draft.addLesson(new Lesson("Lesson 1", "summary", "https://example.com/l1", 1, 10));
    }

    @Test
    void listCourses_returnsOnlyPublishedWhenRequested() {
        when(courseRepository.findByPublishedTrue()).thenReturn(List.of(published));

        var actual = courseService.listCourses(null, false);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).isPublished()).isTrue();
    }

    @Test
    void getCourseDetail_ordersLessonsBySequence() {
        published.addLesson(new Lesson("First", "sum", "url", 2, 15));
        published.addLesson(new Lesson("Second", "sum", "url", 1, 20));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(published));
        when(lessonRepository.findByCourseId(1L)).thenReturn(published.getLessons());
        when(reviewRepository.findByCourseId(1L)).thenReturn(List.of());

        var actual = courseService.getCourseDetail(1L);

        assertThat(actual.getLessons())
                .extracting(lessonDto -> lessonDto.getSequenceNumber())
                .containsExactly(1, 2);
    }
}
