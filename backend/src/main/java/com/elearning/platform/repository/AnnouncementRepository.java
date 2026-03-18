package com.elearning.platform.repository;

import com.elearning.platform.domain.Announcement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByCourseIdOrderByPostedAtDesc(Long courseId);
}
