package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoAnnotation;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.persistence.entity.Annotation;
import com.nbu.annotationplus.persistence.repository.AnnotationCategoryRepository;
import com.nbu.annotationplus.persistence.repository.AnnotationRepository;
import com.nbu.annotationplus.persistence.repository.CommentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class AnnotationService {

    @Autowired
    private NoteService noteService;

    @Autowired
    private AnnotationCategoryService annotationCategoryService;

    @Autowired
    private AnnotationRepository annotationRepository;

    @Autowired
    private CommentRepository commentRepository;

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
        annotationCategoryService.getAnnotationCategory(dtoAnnotation.getAnnotationCategoryId());
        noteService.getNoteById(dtoAnnotation.getNoteId());
        if(!annotationRepository.findByUid(dtoAnnotation.getUid().trim()).isPresent()){
            Annotation annotation = new Annotation();
            annotation.setAnnotationCategoryId(dtoAnnotation.getAnnotationCategoryId());
            annotation.setUserId(currentUserId);
            annotation.setNoteId(dtoAnnotation.getNoteId());
            annotation.setUsername(userName);
            if(dtoAnnotation.getUid() == null || dtoAnnotation.getUid().trim().equals("")){
                throw new InvalidInputParamsException("Invalid UID");
            }
            annotation.setUid(dtoAnnotation.getUid().trim());
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
        }else{
            throw new InvalidInputParamsException("Annotation with Annotation UID: " + "'" + dtoAnnotation.getUid().trim() + "'" + " already exists.");
        }
    }

    @Transactional
    public DtoAnnotation getAnnotationByAnnotationId(String uid) {
        Long currentUserId = userService.getUserId();
        Annotation annotation = annotationRepository.findByUidAndUserId(uid, currentUserId);
        if(annotation == null){
            throw new ResourceNotFoundException("Annotation", "UID",uid);
        }
        return toDtoAnnotation(annotation);
    }

    @Transactional
    public List<DtoAnnotation> getAnnotationsByAnnotationCategoryIdAndUserId(Long annotationCategoryId) {
        Long currentUserId = userService.getUserId();
        List<DtoAnnotation> list;
        list = new ArrayList<>();
        List<Annotation> annotationList = annotationRepository.findByAnnotationCategoryIdAndUserIdOrderByCreatedTsDesc(annotationCategoryId, currentUserId);
        for(Annotation annotation: annotationList){
            list.add(toDtoAnnotation(annotation));
        }
        return list;
    }

    @Transactional
    public ResponseEntity<?> deleteAnnotation(String uid) {
        Long currentUserId = userService.getUserId();
        Annotation annotation = annotationRepository.findByUidAndUserId(uid, currentUserId);
        if(annotation == null){
            throw new ResourceNotFoundException("Annotation", "UID",uid);
        }
        annotationRepository.deleteByUid(uid);
        commentRepository.deleteByAnnotationUid(uid);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public DtoAnnotation updateAnnotation(String annotationUid, DtoAnnotation dtoAnnotation) {
        Long currentUserId = userService.getUserId();
        Annotation annotation = annotationRepository.findByUidAndUserId(annotationUid, currentUserId);
        if(annotation == null){
            throw new ResourceNotFoundException("Annotation", "UID",annotationUid);
        }
        if(dtoAnnotation.getColor() != null && !dtoAnnotation.getColor().trim().equals("")){
            if(!annotation.getColor().equals(dtoAnnotation.getColor().trim())){
                annotation.setColor(dtoAnnotation.getColor().trim());
            }
        }
        annotationRepository.save(annotation);
        return toDtoAnnotation(annotation);
    }
}
