package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.dto.DtoSource;
import com.nbu.annotationplus.service.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SourceController {

    @Autowired
    private SourceService sourceService;

    @PostMapping("/source")
    public ResponseEntity<DtoSource> createSource(@Valid @RequestBody DtoSource dtoSource) {
        return sourceService.createSource(dtoSource);
    }

    @GetMapping("/source/{id}")
    public DtoSource getSourceById(@PathVariable(value = "id") Long id) {
        return sourceService.getSourceById(id);
    }


    @GetMapping("/source")
    public List<DtoSource> getAllSources(@RequestParam(required = false) Long categoryId) {
        return sourceService.getAllSources(categoryId);
    }

    @PutMapping("/source/{id}")
    public DtoSource updateSourceById(@PathVariable(value = "id") Long id, @Valid @RequestBody DtoSource dtoSource) {
        return sourceService.updateSourceById(id,dtoSource);
    }

    @DeleteMapping("/source/{id}")
    public ResponseEntity<?> deleteSourceById(@PathVariable(value = "id") Long id) {
        return sourceService.deleteSourceById(id);
    }
}
