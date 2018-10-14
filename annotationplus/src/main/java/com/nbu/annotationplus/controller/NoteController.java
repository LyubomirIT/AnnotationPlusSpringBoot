package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.model.Note;
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
    public ResponseEntity<Note> createNote(@Valid @RequestBody Note note) {
        return noteService.createNote(note);
    }

    @GetMapping("/notes/{id}")
    public Note getNoteById(@PathVariable(value = "id") Long noteId) {
        return noteService.getNoteById(noteId);
    }

    @GetMapping("/notes")
    public List <Note> getNotesByUserId() {
        return noteService.getNotesByUserId();

    }

    @PutMapping("/notes/{id}")
    public Note updateNote(@PathVariable(value = "id") Long noteId,
                           @Valid @RequestBody Note noteDetails) {
        return noteService.updateNote(noteId,noteDetails);
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable(value = "id") Long noteId) {
        return noteService.deleteNote(noteId);
    }

}
