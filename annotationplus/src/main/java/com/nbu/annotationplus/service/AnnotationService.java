package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoAnnotation;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.persistence.entity.Annotation;
import com.nbu.annotationplus.persistence.entity.User;
import com.nbu.annotationplus.persistence.repository.AnnotationCategoryRepository;
import com.nbu.annotationplus.persistence.repository.AnnotationRepository;
import com.nbu.annotationplus.persistence.repository.CommentRepository;
import com.nbu.annotationplus.persistence.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class AnnotationService {

    @Autowired
    private NoteService noteService;

    @Autowired
    private AnnotationRepository annotationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AnnotationCategoryRepository annotationCategoryRepository;

    private DtoAnnotation toDtoAnnotation(Annotation annotation) {
        ModelMapper modelMapper = new ModelMapper();
        DtoAnnotation dtoAnnotation = modelMapper.map(annotation, DtoAnnotation.class);
        return dtoAnnotation;
    }

    @Transactional
    public ResponseEntity<DtoAnnotation> createAnnotation(DtoAnnotation dtoAnnotation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        Annotation annotation = new Annotation();
        Annotation existingAnnotation = annotationRepository.findAnnotationByAnnotationId(dtoAnnotation.getAnnotationId().trim());
        if(existingAnnotation == null){
            annotationCategoryRepository.findById(dtoAnnotation.getAnnotationCategoryId()) .orElseThrow(() -> new ResourceNotFoundException("Annotation Category", "id", dtoAnnotation.getAnnotationCategoryId()));
            annotation.setAnnotationCategoryId(dtoAnnotation.getAnnotationCategoryId());
            annotation.setUserId(userId);
            noteService.getNoteById(dtoAnnotation.getNoteId());
            annotation.setNoteId(dtoAnnotation.getNoteId());
            annotation.setUsername(userName);
            annotation.setAnnotationId(dtoAnnotation.getAnnotationId().trim());
            annotation.setContent(dtoAnnotation.getContent());
            annotation.setColor(dtoAnnotation.getColor());
            annotationRepository.save(annotation);
            return new ResponseEntity<DtoAnnotation>(toDtoAnnotation(annotation), HttpStatus.CREATED);
        }else{
            throw new InvalidInputParamsException("Annotation with Annotation Id: " + "'" + dtoAnnotation.getAnnotationId().trim() + "'" + " already exists.");
        }
    }

    @Transactional
    public DtoAnnotation getAnnotationByAnnotationId(String annotationId) {
        Annotation annotation = annotationRepository.findAnnotationByAnnotationId(annotationId);
        if(annotation == null){
            throw new ResourceNotFoundException("Annotation", "id",annotationId);
        }
        return toDtoAnnotation(annotation);
    }

    @Transactional
    public List<DtoAnnotation> getAnnotationsByAnnotationCategoryIdAndUserId(Long annotationCategoryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        List<DtoAnnotation> list;
        list = new ArrayList<>();
        List<Annotation> annotationList = annotationRepository.findAnnotationsByAnnotationCategoryIdAndUserIdOrderByCreatedTsDesc(annotationCategoryId, userId);
        for(Annotation annotation: annotationList){
            list.add(toDtoAnnotation(annotation));
        }
        return list;
    }

    @Transactional
    public ResponseEntity<?> deleteAnnotation(String annotationId) {
        Annotation annotation = annotationRepository.findAnnotationByAnnotationId(annotationId);
        if(annotation == null){
            throw new ResourceNotFoundException("Annotation", "annotation-id",annotationId);
        }
        annotationRepository.deleteAnnotationByAnnotationId(annotationId);
        commentRepository.deleteAllByAnnotationId(annotationId);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public DtoAnnotation updateAnnotation(String annotationId, DtoAnnotation dtoAnnotation) {
        Annotation annotation = annotationRepository.findAnnotationByAnnotationId(annotationId);
        if(annotation == null){
            throw new ResourceNotFoundException("Annotation", "annotation-id",annotationId);
        }
        if(dtoAnnotation.getColor() == null){
            dtoAnnotation.setColor(annotation.getColor());
        }
        annotation.setColor(dtoAnnotation.getColor());
        annotationRepository.save(annotation);
        return toDtoAnnotation(annotation);
    }
}
