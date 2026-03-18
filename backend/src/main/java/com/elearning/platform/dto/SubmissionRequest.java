package com.elearning.platform.dto;

import jakarta.validation.constraints.NotBlank;

public class SubmissionRequest {

    @NotBlank
    private String fileUrl;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
