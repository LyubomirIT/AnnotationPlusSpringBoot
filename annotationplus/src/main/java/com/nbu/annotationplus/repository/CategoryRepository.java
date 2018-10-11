package com.nbu.annotationplus.repository;

import com.nbu.annotationplus.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long>  {
    List<Category> findAllByUserId(Long userId);
}
