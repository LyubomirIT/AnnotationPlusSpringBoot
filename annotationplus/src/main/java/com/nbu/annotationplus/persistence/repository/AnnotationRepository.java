package com.nbu.annotationplus.persistence.repository;

import com.nbu.annotationplus.persistence.entity.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
    Optional<Annotation> findByUid(String annotationUid);
    Annotation findByUidAndUserId(String annotationUid, Long userId);
    void deleteByUid(String annotationUid);
    List<Annotation> findByAnnotationCategoryIdAndUserIdOrderByCreatedTsDesc(Long categoryId, Long UserId);
}
