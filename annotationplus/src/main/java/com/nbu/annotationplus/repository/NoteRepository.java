package com.nbu.annotationplus.repository;

import com.nbu.annotationplus.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by rajeevkumarsingh on 27/06/17.
 */

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    //public String eror = "error";
    List<Note> findByUserId(Long userId);

}
