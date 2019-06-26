package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.dto.DtoCategory;
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
    public List<DtoCategory> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping("/category")
    public ResponseEntity<DtoCategory> createCategory(@Valid @RequestBody DtoCategory dtoCategory) {
        return categoryService.createCategory(dtoCategory);
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable(value = "id") Long categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    @PutMapping("/category/{id}")
    public DtoCategory updateCategory(@PathVariable(value = "id") Long categoryId,
                           @Valid @RequestBody DtoCategory dtoCategory) {
        return categoryService.updateCategory(categoryId,dtoCategory);
    }

    @GetMapping("/category/{id}")
    public DtoCategory getCategoryById(@PathVariable(value = "id") Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }
}
