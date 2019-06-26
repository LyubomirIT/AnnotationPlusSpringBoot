package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.dto.DtoNote;
import com.nbu.annotationplus.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping("/notes")
    public ResponseEntity<DtoNote> createNote(@Valid @RequestBody DtoNote dtoNote) {
        return noteService.createNote(dtoNote);
    }

    @GetMapping("/notes/{id}")
    public DtoNote getNoteById(@PathVariable(value = "id") Long noteId) {
        return noteService.getNoteById(noteId);
    }

    @GetMapping("/notes")
    public List <DtoNote> getNotesByUserId() {
        return noteService.getNotesByUserId();

    }

    @GetMapping("/notes/category={categoryId}")
    public List <DtoNote> getNotesByUserId(@PathVariable(value = "categoryId") Long categoryId) {
        return noteService.getNotesByCategoryIdAndUserId(categoryId);

    }

    @PutMapping("/notes/{id}")
    public DtoNote updateNote(@PathVariable(value = "id") Long noteId,
                           @Valid @RequestBody DtoNote dtoNote) {
        return noteService.updateNote(noteId,dtoNote);
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable(value = "id") Long noteId) {
        return noteService.deleteNote(noteId);
    }

}
