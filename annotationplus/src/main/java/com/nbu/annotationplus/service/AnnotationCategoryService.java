package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoAnnotationCategory;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.persistence.entity.AnnotationCategory;
import com.nbu.annotationplus.persistence.entity.User;
import com.nbu.annotationplus.persistence.repository.AnnotationCategoryRepository;
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
public class AnnotationCategoryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnnotationCategoryRepository annotationCategoryRepository;

    private DtoAnnotationCategory toDtoAnnotationCategory(AnnotationCategory annotationCategory){
        ModelMapper modelMapper = new ModelMapper();
        DtoAnnotationCategory dtoAnnotationCategory = modelMapper.map(annotationCategory, DtoAnnotationCategory.class);
        return dtoAnnotationCategory;
    }

    @Transactional
    public List<DtoAnnotationCategory> getAllAnnotationCategories(Long noteId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        List<DtoAnnotationCategory> list;
        list = new ArrayList<>();
        List<AnnotationCategory> annotationCategoryList = annotationCategoryRepository.findAllByUserIdAndNoteIdOrderByCreatedTsDesc(userId,noteId);
        for(AnnotationCategory annotationCategory: annotationCategoryList){
            list.add(toDtoAnnotationCategory(annotationCategory));
        }
        return list;
    }

  /*  @Transactional
    public DtoAnnotationCategory getAnnotationCategoryById(Long noteId, String name){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        AnnotationCategory annotationCategory = annotationCategoryRepository.fi
    }*/

    @Transactional
    public ResponseEntity<DtoAnnotationCategory> createAnnotationCategory(DtoAnnotationCategory dtoAnnotationCategory){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        AnnotationCategory existingAnnotationCategory = annotationCategoryRepository.findByNameAndNoteIdAndUserId(dtoAnnotationCategory.getName(),dtoAnnotationCategory.getNoteId(),userId);
        if (existingAnnotationCategory == null) {
            AnnotationCategory annotationCategory = new AnnotationCategory();
            annotationCategory.setUserId(userId);
            annotationCategory.setName(dtoAnnotationCategory.getName());
            annotationCategory.setNoteId(dtoAnnotationCategory.getNoteId());
            annotationCategoryRepository.save(annotationCategory);
            return new ResponseEntity<DtoAnnotationCategory>(toDtoAnnotationCategory(annotationCategory), HttpStatus.CREATED);
        } else {
            throw new InvalidInputParamsException("Annotation Category with name: " + "'" + dtoAnnotationCategory.getName() + "'" + " already exists.");
        }
    }
}
