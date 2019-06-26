package com.nbu.annotationplus.persistence.repository;

import com.nbu.annotationplus.persistence.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAnnotationIdOrderByCreatedTsDesc(String annotationId);
    void deleteAllByNoteId(Long noteId);
    void deleteAllByAnnotationId(String annotationId);
    void deleteByIdAndUserId(Long id, Long userId);
    Comment findByIdAndUserId(Long id, Long userId);
}
