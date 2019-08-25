package com.nbu.annotationplus.persistence.repository;

import com.nbu.annotationplus.persistence.entity.AnnotationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnotationCategoryRepository extends JpaRepository<AnnotationCategory, Long> {
    List<AnnotationCategory> findByUserIdAndNoteIdOrderByCreatedTsDesc(Long UserId, Long NoteId);
    Optional<AnnotationCategory> findByNameAndNoteIdAndUserId(String Name, Long NoteId, Long UserId);
    void deleteByIdAndUserId(Long id,Long userId);
    AnnotationCategory findByIdAndUserId(Long id,Long userId);
}
