package com.elearning.platform.service;

import com.elearning.platform.domain.Category;
import com.elearning.platform.dto.CategoryDTO;
import com.elearning.platform.dto.CategoryRequest;
import com.elearning.platform.repository.CategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> list() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public CategoryDTO create(CategoryRequest request, Long actorId) {
        categoryRepository.findByName(request.getName()).ifPresent(existing -> {
            throw new IllegalArgumentException("Category already exists");
        });
        Category category = new Category(request.getName(), request.getDescription());
        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryRequest request, Long actorId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return toDTO(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id, Long actorId) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    private CategoryDTO toDTO(Category category) {
        return new CategoryDTO(category.getId(), category.getName(), category.getDescription());
    }
}
