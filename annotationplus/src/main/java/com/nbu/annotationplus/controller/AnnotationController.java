package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.dto.DtoAnnotation;
import com.nbu.annotationplus.service.AnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AnnotationController {

    @Autowired
    private AnnotationService annotationService;

    @PostMapping("/annotation")
    public ResponseEntity<DtoAnnotation> createAnnotation(@Valid @RequestBody DtoAnnotation dtoAnnotation) {
        return annotationService.createAnnotation(dtoAnnotation);
    }

    @PutMapping("/annotation/{id}")
    public DtoAnnotation updateAnnotation(@PathVariable(value = "id") Long id,
                                          @Valid @RequestBody DtoAnnotation dtoAnnotation){
        return annotationService.updateAnnotation(id, dtoAnnotation);
    }

    @GetMapping("/annotation/{id}")
    public DtoAnnotation getAnnotationById(@PathVariable(value = "id") Long id) {
        return annotationService.getAnnotationById(id);
    }

    @GetMapping("/annotation")
    public List<DtoAnnotation> getAnnotations(@RequestParam(required = false) Long annotationCategoryId) {
        return annotationService.getAnnotationsByAnnotationCategoryIdAndUserId(annotationCategoryId);
    }

    @DeleteMapping("/annotation/{id}")
    public ResponseEntity<?> deleteAnnotation(@PathVariable(value = "id") Long id) {
        return annotationService.deleteAnnotation(id);
    }
}
