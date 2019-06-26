package com.nbu.annotationplus.persistence.repository;

import com.nbu.annotationplus.persistence.entity.AnnotationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("annotationCategoryRepository")
public interface AnnotationCategoryRepository extends JpaRepository<AnnotationCategory, Long> {
    List<AnnotationCategory> findAllByUserIdAndNoteIdOrderByCreatedTsDesc(Long UserId, Long NoteId);
    AnnotationCategory findByNameAndNoteIdAndUserId(String Name,Long NoteId,Long UserId);
    AnnotationCategory findByName(String name);
}
