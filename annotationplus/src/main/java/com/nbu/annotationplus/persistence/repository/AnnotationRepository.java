package com.nbu.annotationplus.persistence.repository;

import com.nbu.annotationplus.persistence.entity.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
    Annotation findAnnotationByAnnotationId(String annotationId);
    void deleteAnnotationByAnnotationId(String annotationId);
    List<Annotation> findAnnotationsByAnnotationCategoryIdAndUserIdOrderByCreatedTsDesc(Long categoryId, Long UserId);
}
