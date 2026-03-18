package com.elearning.platform.dto;

import java.time.LocalDateTime;

public class AnnouncementDTO {
    private Long id;
    private String title;
    private String body;
    private String instructorName;
    private LocalDateTime postedAt;

    public AnnouncementDTO() {}

    public AnnouncementDTO(Long id, String title, String body, String instructorName, LocalDateTime postedAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.instructorName = instructorName;
        this.postedAt = postedAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getInstructorName() { return instructorName; }
    public LocalDateTime getPostedAt() { return postedAt; }
}
