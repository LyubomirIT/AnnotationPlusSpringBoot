package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoCategory;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.persistence.entity.Category;
import com.nbu.annotationplus.persistence.entity.User;
import com.nbu.annotationplus.persistence.repository.CategoryRepository;
import com.nbu.annotationplus.persistence.repository.NoteRepository;
import com.nbu.annotationplus.persistence.repository.UserRepository;
import com.nbu.annotationplus.utils.ParseUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    private DtoCategory toDtoCategory(Category category) {
        ModelMapper modelMapper = new ModelMapper();
        DtoCategory dtoCategory = modelMapper.map(category, DtoCategory.class);
        return dtoCategory;
    }

    @Transactional
    public List<DtoCategory> getAllCategories() {
       // validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        List<DtoCategory> list;
        list = new ArrayList<>();
        List<Category> categoryList = categoryRepository.findAllByUserId(userId);
        for(Category category: categoryList){
            list.add(toDtoCategory(category));
        }
        return list;
    }

    @Transactional
    public ResponseEntity<DtoCategory> createCategory(DtoCategory dtoCategory) {
        //validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        Category category = new Category();
        category.setUserId(userId);
        validateCategory(dtoCategory);
        category.setName(dtoCategory.getName().trim());
        Category existingCategory = categoryRepository.findByNameAndUserId(dtoCategory.getName().trim(),userId);
        if(existingCategory == null){
            categoryRepository.save(category);
            return new ResponseEntity<DtoCategory>(toDtoCategory(category), HttpStatus.CREATED);
        }else {
            throw new InvalidInputParamsException("Category with name: " + "'" + dtoCategory.getName() + "'" + " already exists.");
        }
    }

    @Transactional
    public ResponseEntity<?> deleteCategory(Long categoryId) {
       // validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        Long categoryUserId = category.getUserId();
        if(userId.equals(categoryUserId)){
            categoryRepository.delete(category);
            noteRepository.deleteAllByCategoryId(categoryId);
            return ResponseEntity.ok().build();
        }else{
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
    }

    @Transactional
    public DtoCategory updateCategory(Long categoryId, DtoCategory dtoCategory) {
       // validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        Long categoryUserId = category.getUserId();
        if(userId.equals(categoryUserId)){
            validateCategory(dtoCategory);
            Category existingCategory = categoryRepository.findByNameAndUserId(dtoCategory.getName().trim(),userId);
            if (existingCategory == null || existingCategory.getId().equals(categoryId)){
                category.setUserId(category.getUserId());
                category.setName(dtoCategory.getName().trim());
                category.setUpdatedTs(LocalDateTime.now(Clock.systemUTC()));
                categoryRepository.save(category);
                return toDtoCategory(category);
            }else {
                throw new InvalidInputParamsException("Category with name: " + "'" + dtoCategory.getName() + "'" + " already exists.");
            }
        }else{
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
    }

    @Transactional
    public DtoCategory getCategoryById(Long categoryId) {
        //validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        Long categoryUserId = category.getUserId();
        if(userId .equals(categoryUserId)){
            return toDtoCategory(category);
        }else{
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
    }

    private void validateCategory(DtoCategory dtoCategory){
        if(ParseUtils.validateTitle(dtoCategory.getName())){
            throw new InvalidInputParamsException("Invalid Name");
        }
    }

  /*  private void validateUser(){
        Authentication authentication = AuthUtils.getAuthenticateduser();
        if(authentication == null){
            throw new UnauthorizedException("Unauthorized");
        }
    }*/
}
