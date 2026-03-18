package com.elearning.platform.dto;

import jakarta.validation.constraints.NotBlank;

public class ThreadRequest {

    @NotBlank
    private String topic;

    private Long courseId;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
