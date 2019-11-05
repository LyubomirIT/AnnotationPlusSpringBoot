package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoAnnotation;
import com.nbu.annotationplus.dto.DtoAnnotationCategory;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.persistence.entity.Annotation;
import com.nbu.annotationplus.persistence.repository.AnnotationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class AnnotationService {

    @Autowired
    private AnnotationCategoryService annotationCategoryService;

    @Autowired
    private AnnotationRepository annotationRepository;

    @Autowired
    private UserService userService;

    private DtoAnnotation toDtoAnnotation(Annotation annotation) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(annotation, DtoAnnotation.class);
    }

    @Transactional
    public ResponseEntity<DtoAnnotation> createAnnotation(DtoAnnotation dtoAnnotation) {
        Long currentUserId = userService.getUserId();
        String userName = userService.getCurrentUser().getEmail();
        if(dtoAnnotation.getAnnotationCategoryId() == null){
            throw new InvalidInputParamsException("Annotation Category Id is required");
        }
        DtoAnnotationCategory dtoAnnotationCategory = annotationCategoryService.getAnnotationCategoryById(dtoAnnotation.getAnnotationCategoryId());
            Annotation annotation = new Annotation();
            annotation.setAnnotationCategoryId(dtoAnnotation.getAnnotationCategoryId());
            annotation.setUserId(currentUserId);
            annotation.setNoteId(dtoAnnotationCategory.getNoteId());
            annotation.setUsername(userName);
            if(dtoAnnotation.getContent() == null || dtoAnnotation.getContent().trim().equals("")){
                throw new InvalidInputParamsException("Invalid Content");
            }
            annotation.setContent(dtoAnnotation.getContent());
            if(dtoAnnotation.getColor() == null || dtoAnnotation.getColor().trim().equals("")){
                throw new InvalidInputParamsException("Invalid Color");
            }
            annotation.setColor(dtoAnnotation.getColor());
            annotationRepository.save(annotation);
            return new ResponseEntity<DtoAnnotation>(toDtoAnnotation(annotation), HttpStatus.CREATED);
    }

    @Transactional
    public DtoAnnotation getAnnotationById(Long id) {
        Long currentUserId = userService.getUserId();
        Annotation annotation = annotationRepository.findByIdAndUserId(id, currentUserId);
        if(annotation == null){
            throw new ResourceNotFoundException("Annotation", "Id",id);
        }
        return toDtoAnnotation(annotation);
    }

    @Transactional
    public List<DtoAnnotation> getAllAnnotations(Long annotationCategoryId) {
        Long currentUserId = userService.getUserId();
        List<DtoAnnotation> list;
        list = new ArrayList<>();
        List<Annotation> annotationList;
        if(annotationCategoryId == null){
            annotationList = annotationRepository.findByUserIdOrderByIdDesc(currentUserId);
            for(Annotation annotation: annotationList){
                list.add(toDtoAnnotation(annotation));
            }
            return list;
        }else {
            annotationList = annotationRepository.findByAnnotationCategoryIdAndUserIdOrderByCreatedTsDesc(annotationCategoryId, currentUserId);
            for (Annotation annotation : annotationList) {
                list.add(toDtoAnnotation(annotation));
            }
            return list;
        }
    }

    @Transactional
    public ResponseEntity<?> deleteAnnotationById(Long id) {
        Long currentUserId = userService.getUserId();
        Annotation annotation = annotationRepository.findByIdAndUserId(id, currentUserId);
        if(annotation == null){
            throw new ResourceNotFoundException("Annotation", "Id",id);
        }
        annotationRepository.deleteByIdAndUserId(id, currentUserId);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public DtoAnnotation updateAnnotationById(Long id, DtoAnnotation dtoAnnotation) {
        Long currentUserId = userService.getUserId();
        Annotation annotation = annotationRepository.findByIdAndUserId(id, currentUserId);
        if(annotation == null){
            throw new ResourceNotFoundException("Annotation", "Id",id);
        }
        if(dtoAnnotation.getColor() != null && !dtoAnnotation.getColor().trim().equals("")){
            if(!annotation.getColor().equals(dtoAnnotation.getColor().trim())){
                annotation.setColor(dtoAnnotation.getColor().trim());
                annotation.setUpdatedTs(LocalDateTime.now(Clock.systemUTC()));
            }
        }
        annotationRepository.save(annotation);
        return toDtoAnnotation(annotation);
    }
}
