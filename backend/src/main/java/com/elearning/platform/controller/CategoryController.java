package com.elearning.platform.controller;

import com.elearning.platform.dto.CategoryDTO;
import com.elearning.platform.dto.CategoryRequest;
import com.elearning.platform.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.elearning.platform.security.UserDetailsImpl;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDTO> list() {
        return categoryService.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDTO create(@AuthenticationPrincipal UserDetailsImpl userDetails,
                              @Valid @RequestBody CategoryRequest request) {
        return categoryService.create(request, userDetails.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDTO update(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetailsImpl userDetails,
                              @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request, userDetails.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        categoryService.delete(id, userDetails.getId());
    }
}
