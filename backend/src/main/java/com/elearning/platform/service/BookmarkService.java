package com.elearning.platform.service;

import com.elearning.platform.domain.Bookmark;
import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.UserAccount;
import com.elearning.platform.dto.BookmarkDTO;
import com.elearning.platform.repository.BookmarkRepository;
import com.elearning.platform.repository.CourseRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final CourseRepository courseRepository;
    private final UserService userService;

    public BookmarkService(BookmarkRepository bookmarkRepository,
                           CourseRepository courseRepository,
                           UserService userService) {
        this.bookmarkRepository = bookmarkRepository;
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    @Transactional
    public BookmarkDTO addBookmark(Long userId, Long courseId) {
        if (bookmarkRepository.findByUserIdAndCourseId(userId, courseId).isPresent()) {
            throw new IllegalStateException("Course already bookmarked");
        }
        UserAccount user = userService.findOne(userId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        Bookmark saved = bookmarkRepository.save(new Bookmark(user, course));
        return toDTO(saved);
    }

    @Transactional
    public void removeBookmark(Long userId, Long courseId) {
        bookmarkRepository.deleteByUserIdAndCourseId(userId, courseId);
    }

    @Transactional(readOnly = true)
    public List<BookmarkDTO> getBookmarks(Long userId) {
        return bookmarkRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private BookmarkDTO toDTO(Bookmark b) {
        return new BookmarkDTO(b.getCourse().getId(), b.getCourse().getTitle(), b.getSavedAt());
    }
}
