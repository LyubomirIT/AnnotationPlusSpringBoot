package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoAnnotationCategory;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.persistence.entity.AnnotationCategory;
import com.nbu.annotationplus.persistence.repository.AnnotationCategoryRepository;
import com.nbu.annotationplus.persistence.repository.SourceRepository;
import com.nbu.annotationplus.utils.ValidationUtils;
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
public class AnnotationCategoryService {

    @Autowired
    private AnnotationCategoryRepository annotationCategoryRepository;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private UserService userService;

    private DtoAnnotationCategory toDtoAnnotationCategory(AnnotationCategory annotationCategory){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(annotationCategory, DtoAnnotationCategory.class);
    }

    @Transactional
    public List<DtoAnnotationCategory> getAllAnnotationCategories(Long sourceId){
        Long currentUserId = userService.getUserId();
        List<DtoAnnotationCategory> list;
        list = new ArrayList<>();
        List<AnnotationCategory> annotationCategoryList;
        if(sourceId == null){
            annotationCategoryList = annotationCategoryRepository.findByUserIdOrderByCreatedTsDesc(currentUserId);
            for(AnnotationCategory annotationCategory: annotationCategoryList){
                list.add(toDtoAnnotationCategory(annotationCategory));
            }
            return list;
        }else {
            annotationCategoryList = annotationCategoryRepository.findByUserIdAndSourceIdOrderByCreatedTsDesc(currentUserId,sourceId);
            for (AnnotationCategory annotationCategory : annotationCategoryList) {
                list.add(toDtoAnnotationCategory(annotationCategory));
            }
            return list;
        }
    }

    @Transactional
    public DtoAnnotationCategory getAnnotationCategoryById(Long id){
        Long currentUserId = userService.getUserId();
        AnnotationCategory annotationCategory = annotationCategoryRepository.findByIdAndUserId(id,currentUserId);
        if(annotationCategory == null){
            throw new ResourceNotFoundException("Annotation Category", "id", id);
        }
        return toDtoAnnotationCategory(annotationCategory);
    }

    @Transactional
    public ResponseEntity<?> deleteAnnotationCategoryById(Long id){
        Long currentUserId = userService.getUserId();
        AnnotationCategory annotationCategory = annotationCategoryRepository.findByIdAndUserId(id,currentUserId);
        if(annotationCategory== null){
            throw new ResourceNotFoundException("Annotation Category", "id", id);
        }
        else{
           annotationCategoryRepository.deleteByIdAndUserId(id,currentUserId);
           return ResponseEntity.ok().build();
        }
    }

    @Transactional
    public DtoAnnotationCategory updateAnnotationCategoryById(Long id, DtoAnnotationCategory dtoAnnotationCategory){
        Long currentUserId = userService.getUserId();
        AnnotationCategory annotationCategory = annotationCategoryRepository.findByIdAndUserId(id,currentUserId);
        if(annotationCategory== null){
            throw new ResourceNotFoundException("Annotation Category", "id", id);
        }
        if(dtoAnnotationCategory.getName() != null && !dtoAnnotationCategory.getName().trim().equals("")){
            if(!annotationCategory.getName().equals(dtoAnnotationCategory.getName().trim())){
                validateAnnotationCategoryName(dtoAnnotationCategory.getName());
                if(annotationCategoryRepository.findByNameAndSourceIdAndUserId(dtoAnnotationCategory.getName(),annotationCategory.getSourceId(),currentUserId).isPresent()){
                    throw new InvalidInputParamsException("Annotation Category with name: " + "'" + dtoAnnotationCategory.getName() + "'" + " already exists.");
                }
                annotationCategory.setName(dtoAnnotationCategory.getName().trim());
                annotationCategory.setUpdatedTs(LocalDateTime.now(Clock.systemUTC()));
            }
        }
        annotationCategoryRepository.save(annotationCategory);
        return toDtoAnnotationCategory(annotationCategory);
    }

    @Transactional
    public ResponseEntity<DtoAnnotationCategory> createAnnotationCategory(DtoAnnotationCategory dtoAnnotationCategory){
        Long currentUserId = userService.getUserId();
        if(dtoAnnotationCategory.getSourceId() == null){
            throw new InvalidInputParamsException("Source Id is required");
        }
        if(sourceRepository.findByIdAndUserId(dtoAnnotationCategory.getSourceId(),currentUserId) == null){
            throw new ResourceNotFoundException("Source", "id", dtoAnnotationCategory.getSourceId());
        }
        validateAnnotationCategoryName(dtoAnnotationCategory.getName());
        if (!annotationCategoryRepository.findByNameAndSourceIdAndUserId(dtoAnnotationCategory.getName(),dtoAnnotationCategory.getSourceId(),currentUserId).isPresent()) {
            AnnotationCategory annotationCategory = new AnnotationCategory();
            annotationCategory.setUserId(currentUserId);
            annotationCategory.setName(dtoAnnotationCategory.getName());
            annotationCategory.setSourceId(dtoAnnotationCategory.getSourceId());
            annotationCategoryRepository.save(annotationCategory);
            return new ResponseEntity<DtoAnnotationCategory>(toDtoAnnotationCategory(annotationCategory), HttpStatus.CREATED);
        } else {
            throw new InvalidInputParamsException("Annotation Category with name: " + "'" + dtoAnnotationCategory.getName() + "'" + " already exists.");
        }

    }

    private void validateAnnotationCategoryName(String annotationCategoryName){
        ValidationUtils.validateName(annotationCategoryName);
    }
}
