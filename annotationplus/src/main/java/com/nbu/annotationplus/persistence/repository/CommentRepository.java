package com.nbu.annotationplus.persistence.repository;

import com.nbu.annotationplus.persistence.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAnnotationIdAndUserIdOrderByCreatedTsDesc(Long annotationId, Long userId);
    List<Comment> findByUserIdOrderByCreatedTsDesc(Long userId);
    //void deleteByAnnotationUidAndUserId(String annotationUid, Long userId);
    void deleteByIdAndUserId(Long id, Long userId);
    Comment findByIdAndUserId(Long id, Long userId);
}
