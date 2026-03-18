package com.elearning.platform.dto;

import jakarta.validation.constraints.NotNull;

public class CoursePublishRequest {

    @NotNull
    private Boolean published;

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }
}
