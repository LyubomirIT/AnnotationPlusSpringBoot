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

    @PostMapping("/note")
    public ResponseEntity<DtoNote> createNote(@Valid @RequestBody DtoNote dtoNote) {
        return noteService.createNote(dtoNote);
    }

    @GetMapping("/note/{id}")
    public DtoNote getNoteById(@PathVariable(value = "id") Long id) {
        return noteService.getNoteById(id);
    }


    @GetMapping("/note")
    public List<DtoNote> getAllNotes(@RequestParam(required = false) Long categoryId) {
        return noteService.getAllNotes(categoryId);
    }

    @PutMapping("/note/{id}")
    public DtoNote updateNoteById(@PathVariable(value = "id") Long id, @Valid @RequestBody DtoNote dtoNote) {
        return noteService.updateNoteById(id,dtoNote);
    }

    @DeleteMapping("/note/{id}")
    public ResponseEntity<?> deleteNoteById(@PathVariable(value = "id") Long id) {
        return noteService.deleteNoteById(id);
    }

}
