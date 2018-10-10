package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.model.Category;
import com.nbu.annotationplus.model.Note;
import com.nbu.annotationplus.repository.CategoryRepository;
import com.nbu.annotationplus.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PostMapping("/category")
    public Category createCategory(@Valid @RequestBody Category category) {
        //return noteRepository.save(note);
        return categoryService.createCategory(category);
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable(value = "id") Long categoryId) {
        //Note note = noteRepository.findById(noteId)
        //.orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        // noteRepository.delete(note);

        // return ResponseEntity.ok().build();
        return categoryService.deleteCategory(categoryId);
    }
}
