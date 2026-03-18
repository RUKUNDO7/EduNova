package com.elearning.platform.repository;

import com.elearning.platform.domain.CommunicationThread;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunicationThreadRepository extends JpaRepository<CommunicationThread, Long> {
    List<CommunicationThread> findByCourseId(Long courseId);
}
