package com.elearning.platform.repository;

import com.elearning.platform.domain.Bookmark;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserIdAndCourseId(Long userId, Long courseId);
    List<Bookmark> findByUserId(Long userId);
    void deleteByUserIdAndCourseId(Long userId, Long courseId);
}
