package com.elearning.platform.repository;

import com.elearning.platform.domain.Assignment;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByCourseId(Long courseId);
    List<Assignment> findByDueDateBeforeAndCourseId(LocalDate date, Long courseId);
    List<Assignment> findByDueDateBefore(LocalDate date);
}
