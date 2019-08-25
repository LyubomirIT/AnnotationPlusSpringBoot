package com.nbu.annotationplus.persistence.repository;

import com.nbu.annotationplus.persistence.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>  {
    List<Category> findByUserId(Long userId);
    Optional<Category> findByNameAndUserId(String Name, Long userId);
    Category findByIdAndUserId(Long id, Long userId);
}
