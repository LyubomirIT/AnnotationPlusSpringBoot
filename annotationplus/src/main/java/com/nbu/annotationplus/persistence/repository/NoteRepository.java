package com.nbu.annotationplus.persistence.repository;

import com.nbu.annotationplus.persistence.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserId(Long userId);
    List<Note> findByCategoryIdAndUserId(Long categoryId, Long userId);
    Optional<Note> findByTitleAndUserId(String title, Long userId);
    Note findByIdAndUserId(Long id, Long userId);
}
