package com.nbu.annotationplus.persistence.repository;

import com.nbu.annotationplus.persistence.entity.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
    Optional<Annotation> findAnnotationByUidAndUserId(String uid,Long userId);
    Annotation findByUidAndUserId(String uid, Long userId);
    void deleteByUidAndUserId(String uid,Long userId);
    List<Annotation> findByAnnotationCategoryIdAndUserIdOrderByCreatedTsDesc(Long annotationCategoryId, Long UserId);
}
