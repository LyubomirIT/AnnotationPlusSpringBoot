package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.dto.DtoAnnotationCategory;
import com.nbu.annotationplus.service.AnnotationCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AnnotationCategoryController {

    @Autowired
    private AnnotationCategoryService annotationCategoryService;

    @PostMapping("/annotationCategory")
    public ResponseEntity<DtoAnnotationCategory> createAnnotationCategory(@Valid @RequestBody DtoAnnotationCategory dtoAnnotationCategory) {
        return annotationCategoryService.createAnnotationCategory(dtoAnnotationCategory);
    }

    @GetMapping("/annotationCategory")
    public List<DtoAnnotationCategory> getAllAnnotationCategories(@RequestParam(required = false) Long sourceId) {
        return annotationCategoryService.getAllAnnotationCategories(sourceId);
    }

    @DeleteMapping("/annotationCategory/{id}")
    ResponseEntity<?> deleteAnnotationCategoryById(@PathVariable(value = "id") Long id) {
        return annotationCategoryService.deleteAnnotationCategoryById(id);
    }

    @PutMapping("/annotationCategory/{id}")
    public DtoAnnotationCategory updateAnnotationCategoryById(@PathVariable(value = "id") Long id, @Valid @RequestBody DtoAnnotationCategory dtoAnnotationCategory) {
        return annotationCategoryService.updateAnnotationCategoryById(id,dtoAnnotationCategory);
    }
}
