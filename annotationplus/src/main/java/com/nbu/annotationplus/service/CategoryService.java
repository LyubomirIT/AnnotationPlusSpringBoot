package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoCategory;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.persistence.entity.Category;
import com.nbu.annotationplus.persistence.repository.CategoryRepository;
import com.nbu.annotationplus.utils.ParseUtils;
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
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    private DtoCategory toDtoCategory(Category category) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(category, DtoCategory.class);
    }

    @Transactional
    public List<DtoCategory> getAllCategories() {
        Long currentUserId = userService.getUserId();
        List<DtoCategory> list;
        list = new ArrayList<>();
        List<Category> categoryList = categoryRepository.findByUserId(currentUserId);
        for(Category category: categoryList){
            list.add(toDtoCategory(category));
        }
        return list;
    }

    @Transactional
    public ResponseEntity<DtoCategory> createCategory(DtoCategory dtoCategory) {
        Long currentUserId = userService.getUserId();
        validateCategoryName(dtoCategory.getName());
        if(!categoryRepository.findByNameAndUserId(dtoCategory.getName().trim(),currentUserId).isPresent()){
            Category category = new Category();
            category.setUserId(currentUserId);
            category.setName(dtoCategory.getName().trim());
            categoryRepository.save(category);
            return new ResponseEntity<DtoCategory>(toDtoCategory(category), HttpStatus.CREATED);
        }else {
            throw new InvalidInputParamsException("Category with name: " + "'" + dtoCategory.getName() + "'" + " already exists.");
        }
    }

    @Transactional
    public ResponseEntity<?> deleteCategory(Long id) {
        Long currentUserId = userService.getUserId();
        Category category = categoryRepository.findByIdAndUserId(id, currentUserId);
        if(category == null){
            throw new ResourceNotFoundException("Category", "id",id);
        }
        else{
            categoryRepository.delete(category);
            return ResponseEntity.ok().build();
        }
    }

    @Transactional
    public DtoCategory updateCategory(Long id, DtoCategory dtoCategory) {
        Long currentUserId = userService.getUserId();
        Category category = categoryRepository.findByIdAndUserId(id,currentUserId);
        if(category == null){
            throw new ResourceNotFoundException("Category", "id", id);
        }
        if(dtoCategory.getName() != null && !dtoCategory.getName().trim().equals("")){
            if(!category.getName().equals(dtoCategory.getName().trim())){
                validateCategoryName(dtoCategory.getName());
                if(categoryRepository.findByNameAndUserId(dtoCategory.getName().trim(),currentUserId).isPresent()){
                    throw new InvalidInputParamsException("Category with name: " + "'" + dtoCategory.getName() + "'" + " already exists.");
                }
                category.setName(dtoCategory.getName().trim());
            }
        }
        category.setUpdatedTs(LocalDateTime.now(Clock.systemUTC()));
        categoryRepository.save(category);
        return toDtoCategory(category);
    }

    @Transactional
    public DtoCategory getCategory(Long id) {
        Long currentUserId = userService.getUserId();
        Category category = categoryRepository.findByIdAndUserId(id, currentUserId);
        if(category == null){
            throw new ResourceNotFoundException("Category", "id", id);
        }
        else{
            return toDtoCategory(category);
        }
    }

    private void validateCategoryName(String categoryName){
        if(ParseUtils.validateTitle(categoryName)){
            throw new InvalidInputParamsException("Invalid Name");
        }
    }
}
