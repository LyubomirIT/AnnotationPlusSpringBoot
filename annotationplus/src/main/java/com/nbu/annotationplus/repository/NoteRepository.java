/*package com.nbu.annotationplus.repository;

import com.nbu.annotationplus.persistence.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserId(Long userId);
    void deleteAllByCategoryId(Long categoryId);
    Note findByTitleAndUserId(String title,long userId);
}
*/