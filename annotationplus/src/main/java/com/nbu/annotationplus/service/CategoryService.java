package com.nbu.annotationplus.service;

import com.nbu.annotationplus.exception.ForbiddenException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.exception.UnauthorizedException;
import com.nbu.annotationplus.model.Category;
import com.nbu.annotationplus.model.Note;
import com.nbu.annotationplus.model.User;
import com.nbu.annotationplus.repository.CategoryRepository;
import com.nbu.annotationplus.repository.UserRepository;
import com.nbu.annotationplus.utils.UserUtils;
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

    @Transactional
    public List<Category> getAllCategories() {
        validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        int userId = user.getId();
        return categoryRepository.findAllByUserId(userId);
    }

    @Transactional
    public ResponseEntity<Category> createCategory(Category category) {
        categoryRepository.save(category);
        return new ResponseEntity<Category>(category, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> deleteCategory(Long categoryId) {
        validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        int userId = user.getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        int categoryUserId = category.getUserId();
        if(userId == categoryUserId){
            categoryRepository.delete(category);
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
        int userId = user.getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        int categoryUserId = category.getUserId();
        if(userId == categoryUserId){
            category.setName(categoryDetails.getName());
            Category updatedCategory = categoryRepository.save(category);
            return updatedCategory;
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
        int userId = user.getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        int categoryUserId = category.getUserId();
        if(userId == categoryUserId){
            return category;
        }else{
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
    }

    private void validateUser(){
        Authentication authentication = UserUtils.getAuthenticateduser();
        if(authentication == null){
            throw new UnauthorizedException("Unauthorized");
        }
    }
}
