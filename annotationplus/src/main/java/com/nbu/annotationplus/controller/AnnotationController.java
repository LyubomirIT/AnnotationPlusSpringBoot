package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.dto.DtoAnnotation;
import com.nbu.annotationplus.dto.DtoAnnotationCategory;
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

    @PutMapping("/annotation/{annotationId}")
    public DtoAnnotation updateAnnotation(@PathVariable(value = "annotationId") String annotationId,
                                          @Valid @RequestBody DtoAnnotation dtoAnnotation){
        return annotationService.updateAnnotation(annotationId, dtoAnnotation);
    }

    @GetMapping("/annotation/{annotationId}")
    public DtoAnnotation getAnnotationById(@PathVariable(value = "annotationId") String annotationId) {
        return annotationService.getAnnotationByAnnotationId(annotationId);
    }

    @GetMapping("/annotation/category={annotationCategoryId}")
    public List<DtoAnnotation> getAnnotationsByAnnotationCategoryIdAndUserId(@PathVariable(value = "annotationCategoryId") Long annotationCategoryId) {
        return annotationService.getAnnotationsByAnnotationCategoryIdAndUserId(annotationCategoryId);
    }

    @DeleteMapping("/annotation/{annotationId}")
    public ResponseEntity<?> deleteAnnotation(@PathVariable(value = "annotationId") String annotationId) {
        return annotationService.deleteAnnotation(annotationId);
    }
}
