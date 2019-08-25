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

    @PutMapping("/annotation/{uid}")
    public DtoAnnotation updateAnnotation(@PathVariable(value = "uid") String uid,
                                          @Valid @RequestBody DtoAnnotation dtoAnnotation){
        return annotationService.updateAnnotation(uid, dtoAnnotation);
    }

    @GetMapping("/annotation/{uid}")
    public DtoAnnotation getAnnotationById(@PathVariable(value = "uid") String uid) {
        return annotationService.getAnnotationByUid(uid);
    }

    @GetMapping("/annotation/category={annotationCategoryId}")
    public List<DtoAnnotation> getAnnotationsByAnnotationCategoryIdAndUserId(@PathVariable(value = "annotationCategoryId") Long annotationCategoryId) {
        return annotationService.getAnnotationsByAnnotationCategoryIdAndUserId(annotationCategoryId);
    }

    @DeleteMapping("/annotation/{uid}")
    public ResponseEntity<?> deleteAnnotation(@PathVariable(value = "uid") String uid) {
        return annotationService.deleteAnnotation(uid);
    }
}
