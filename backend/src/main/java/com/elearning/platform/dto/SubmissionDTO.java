package com.elearning.platform.dto;

import java.time.LocalDateTime;

public class SubmissionDTO {

    private Long id;
    private Long assignmentId;
    private Long studentId;
    private String studentName;
    private String fileUrl;
    private Integer grade;
    private String feedback;
    private String status;
    private LocalDateTime submittedAt;

    public SubmissionDTO(Long id, Long assignmentId, Long studentId, String studentName, String fileUrl, Integer grade, String feedback, String status, LocalDateTime submittedAt) {
        this.id = id;
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.fileUrl = fileUrl;
        this.grade = grade;
        this.feedback = feedback;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public Integer getGrade() {
        return grade;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
}
