package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoAnnotationCategory;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.persistence.entity.AnnotationCategory;
import com.nbu.annotationplus.persistence.repository.AnnotationCategoryRepository;
import com.nbu.annotationplus.persistence.repository.NoteRepository;
import com.nbu.annotationplus.utils.ParseUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnnotationCategoryService {

    @Autowired
    private AnnotationCategoryRepository annotationCategoryRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserService userService;

    private DtoAnnotationCategory toDtoAnnotationCategory(AnnotationCategory annotationCategory){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(annotationCategory, DtoAnnotationCategory.class);
    }

    @Transactional
    public List<DtoAnnotationCategory> getAllAnnotationCategories(Long noteId){
        Long currentUserId = userService.getUserId();
        List<DtoAnnotationCategory> list;
        list = new ArrayList<>();
        List<AnnotationCategory> annotationCategoryList = annotationCategoryRepository.findByUserIdAndNoteIdOrderByCreatedTsDesc(currentUserId,noteId);
        for(AnnotationCategory annotationCategory: annotationCategoryList){
            list.add(toDtoAnnotationCategory(annotationCategory));
        }
        return list;
    }

    @Transactional
    public DtoAnnotationCategory getAnnotationCategory(Long id){
        Long currentUserId = userService.getUserId();
        AnnotationCategory annotationCategory = annotationCategoryRepository.findByIdAndUserId(id,currentUserId);
        if(annotationCategory == null){
            throw new ResourceNotFoundException("Annotation Category", "id", id);

        }
        return toDtoAnnotationCategory(annotationCategory);
    }

    @Transactional
    public ResponseEntity<?> deleteAnnotationCategory(Long id){
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
        Long currentUserId = userService.getUserId();
        if(dtoAnnotationCategory.getNoteId() == null){
            throw new InvalidInputParamsException("Note Id is required");
        }
        if(noteRepository.findByIdAndUserId(dtoAnnotationCategory.getNoteId(),currentUserId) == null){
            throw new ResourceNotFoundException("Note", "id", dtoAnnotationCategory.getNoteId());
        }
        validateAnnotationCategoryName(dtoAnnotationCategory.getName());
        if (!annotationCategoryRepository.findByNameAndNoteIdAndUserId(dtoAnnotationCategory.getName(),dtoAnnotationCategory.getNoteId(),currentUserId).isPresent()) {
            AnnotationCategory annotationCategory = new AnnotationCategory();
            annotationCategory.setUserId(currentUserId);
            annotationCategory.setName(dtoAnnotationCategory.getName());
            annotationCategory.setNoteId(dtoAnnotationCategory.getNoteId());
            annotationCategoryRepository.save(annotationCategory);
            return new ResponseEntity<DtoAnnotationCategory>(toDtoAnnotationCategory(annotationCategory), HttpStatus.CREATED);
        } else {
            throw new InvalidInputParamsException("Annotation Category with name: " + "'" + dtoAnnotationCategory.getName() + "'" + " already exists.");
        }

    }

    private void validateAnnotationCategoryName(String annotationCategoryName){
        if(ParseUtils.validateTitle(annotationCategoryName)){
            throw new InvalidInputParamsException("Invalid Name");
        }
    }
}
