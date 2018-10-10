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
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public ResponseEntity<?> deleteCategory(Long categoryId) {
        validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        int userId = user.getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", categoryId));
        int categoryUserId = category.getUserId();
        if(userId == categoryUserId){
            categoryRepository.delete(category);
            return ResponseEntity.ok().build();
        }else{
            throw new ForbiddenException("Forbidden");
        }

    }

    private void validateUser(){
        Authentication authentication = UserUtils.getAuthenticateduser();
        if(authentication == null){
            throw new UnauthorizedException("Unauthorized");
        }
    }
}
