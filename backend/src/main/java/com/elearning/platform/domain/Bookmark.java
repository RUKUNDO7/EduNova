package com.elearning.platform.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookmarks")
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private LocalDateTime savedAt = LocalDateTime.now();

    public Bookmark() {}

    public Bookmark(UserAccount user, Course course) {
        this.user = user;
        this.course = course;
    }

    public Long getId() { return id; }
    public UserAccount getUser() { return user; }
    public Course getCourse() { return course; }
    public LocalDateTime getSavedAt() { return savedAt; }
}
