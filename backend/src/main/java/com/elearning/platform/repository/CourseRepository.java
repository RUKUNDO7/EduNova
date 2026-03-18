package com.elearning.platform.repository;

import com.elearning.platform.domain.Course;
import com.elearning.platform.domain.Level;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByLevel(Level level);
    List<Course> findByPublishedTrue();

    @Query("SELECT c FROM Course c WHERE (LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND c.published = true")
    List<Course> searchCourses(@Param("query") String query);

    @Query("SELECT c FROM Course c JOIN c.tags t WHERE LOWER(t) = LOWER(:tag) AND c.published = true")
    List<Course> searchCoursesByTag(@Param("tag") String tag);

    List<Course> findByFeaturedTrueAndPublishedTrue();
    List<Course> findByCategoryIdAndIdNotAndPublishedTrue(Long categoryId, Long courseId);
    List<Course> findByInstructorIdAndPublishedTrue(Long instructorId);
}
