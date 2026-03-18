package com.elearning.platform.dto;

public class StudentStatsDTO {
    private Long studentId;
    private String studentName;
    private Integer totalEnrolled;
    private Integer totalCompleted;
    private Double averageQuizScore;

    public StudentStatsDTO() {
    }

    public StudentStatsDTO(Long studentId, String studentName, Integer totalEnrolled, Integer totalCompleted, Double averageQuizScore) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.totalEnrolled = totalEnrolled;
        this.totalCompleted = totalCompleted;
        this.averageQuizScore = averageQuizScore;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public Integer getTotalEnrolled() {
        return totalEnrolled;
    }

    public Integer getTotalCompleted() {
        return totalCompleted;
    }

    public Double getAverageQuizScore() {
        return averageQuizScore;
    }
}
