package com.nbu.annotationplus.persistence.repository;

import com.nbu.annotationplus.persistence.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SourceRepository extends JpaRepository<Source, Long> {
    List<Source> findByUserId(Long userId);
    List<Source> findByCategoryIdAndUserId(Long categoryId, Long userId);
    Optional<Source> findByNameAndUserId(String name, Long userId);
    Source findByIdAndUserId(Long id, Long userId);
}
