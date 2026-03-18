package com.elearning.platform.dto;

import java.util.Set;
import com.elearning.platform.domain.Level;

public class CourseSummaryDTO {

    private Long id;
    private String title;
    private Level level;
    private Integer estimatedHours;
    private boolean published;
    private CategoryDTO category;
    private UserSummaryDTO instructor;
    private Set<String> tags;

    public CourseSummaryDTO(Long id, String title, Level level, Integer estimatedHours, boolean published, CategoryDTO category, UserSummaryDTO instructor, Set<String> tags) {
        this.id = id;
        this.title = title;
        this.level = level;
        this.estimatedHours = estimatedHours;
        this.published = published;
        this.category = category;
        this.instructor = instructor;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
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

    public Set<String> getTags() {
        return tags;
    }
}
