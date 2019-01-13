package com.nbu.annotationplus.service;

import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.exception.UnauthorizedException;
import com.nbu.annotationplus.model.Category;
import com.nbu.annotationplus.model.User;
import com.nbu.annotationplus.repository.CategoryRepository;
import com.nbu.annotationplus.repository.NoteRepository;
import com.nbu.annotationplus.repository.UserRepository;
import com.nbu.annotationplus.utils.AuthUtils;
import com.nbu.annotationplus.utils.ParseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Transactional
    public List<Category> getAllCategories() {
        validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        return categoryRepository.findAllByUserId(userId);
    }

    @Transactional
    public ResponseEntity<Category> createCategory(Category category) {
        validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        category.setUserId(userId);
        validateCategory(category);
        Category existingCategory = categoryRepository.findByNameAndUserId(category.getName(),userId);
        if(existingCategory == null){
            categoryRepository.save(category);
            return new ResponseEntity<Category>(category, HttpStatus.CREATED);
        }else {
            throw new InvalidInputParamsException("Category with name: " + "'" + category.getName() + "'" + " already exists.");
        }
    }

    @Transactional
    public ResponseEntity<?> deleteCategory(Long categoryId) {
        validateUser();
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
    public Category updateCategory(Long categoryId, Category categoryDetails) {
        validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        Long categoryUserId = category.getUserId();
        if(userId.equals(categoryUserId)){
            validateCategory(category);
            //category.setUserId(category.getUserId());
            //category.setName(categoryDetails.getName());
            Category existingCategory = categoryRepository.findByNameAndUserId(categoryDetails.getName(),userId);
            if (existingCategory == null || existingCategory.getId().equals(categoryId)){
                category.setUserId(category.getUserId());
                category.setName(categoryDetails.getName());
                return categoryRepository.save(category);
            }else {
                throw new InvalidInputParamsException("Category with name: " + "'" + categoryDetails.getName() + "'" + " already exists.");
            }
        }else{
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
    }

    @Transactional
    public Category getCategoryById(Long categoryId) {
        validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        Long categoryUserId = category.getUserId();
        if(userId == categoryUserId){
            return category;
        }else{
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
    }

    private void validateCategory(Category category){
        if(ParseUtils.validateTitle(category.getName())){
            throw new InvalidInputParamsException("Invalid Name");
        }
    }

    private void validateUser(){
        Authentication authentication = AuthUtils.getAuthenticateduser();
        if(authentication == null){
            throw new UnauthorizedException("Unauthorized");
        }
    }
}
