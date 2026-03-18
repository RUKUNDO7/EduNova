package com.elearning.platform.dto;

import com.elearning.platform.domain.Level;
import java.util.List;
import java.util.Set;

public class CourseDetailDTO {

    private Long id;
    private String title;
    private String description;
    private Level level;
    private Integer estimatedHours;
    private boolean published;
    private CategoryDTO category;
    private UserSummaryDTO instructor;
    private List<LessonDTO> lessons;
    private List<MaterialDTO> materials;
    private Double averageRating;
    private Integer totalReviews;
    private Set<String> tags;

    public CourseDetailDTO(Long id, String title, String description, Level level, Integer estimatedHours, boolean published, CategoryDTO category, UserSummaryDTO instructor, List<LessonDTO> lessons, List<MaterialDTO> materials, Double averageRating, Integer totalReviews, Set<String> tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.level = level;
        this.estimatedHours = estimatedHours;
        this.published = published;
        this.category = category;
        this.instructor = instructor;
        this.lessons = lessons;
        this.materials = materials;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Level getLevel() {
        return level;
    }

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public boolean isPublished() {
        return published;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public UserSummaryDTO getInstructor() {
        return instructor;
    }

    public List<LessonDTO> getLessons() {
        return lessons;
    }

    public List<MaterialDTO> getMaterials() {
        return materials;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public Set<String> getTags() {
        return tags;
    }
}

