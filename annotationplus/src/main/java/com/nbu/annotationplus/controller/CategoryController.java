package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.model.Category;
import com.nbu.annotationplus.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping("/category")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable(value = "id") Long categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    @PutMapping("/category/{id}")
    public Category updateCategory(@PathVariable(value = "id") Long categoryId,
                           @Valid @RequestBody Category categoryDetails) {
        return categoryService.updateCategory(categoryId,categoryDetails);
    }

    @GetMapping("/category/{id}")
    public Category getCategoryById(@PathVariable(value = "id") Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }
}
