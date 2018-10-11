package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.exception.UnauthorizedException;
import com.nbu.annotationplus.model.Note;
import com.nbu.annotationplus.model.User;
import com.nbu.annotationplus.repository.NoteRepository;
import com.nbu.annotationplus.repository.UserRepository;
import com.nbu.annotationplus.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class NoteController {
    //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    @Autowired
    private NoteService noteService;

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    UserRepository userRepository;


   /* @GetMapping("/notes")
    public List<Note> getAllNotes(Authentication authentication) {
       authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication:" +authentication);
        if(authentication == null){
            throw new UnauthorizedException("Unauthorized");
        }
        return noteService.getAllNotes();
    }*/

    @PostMapping("/notes")
    public ResponseEntity<Note> createNote(@Valid @RequestBody Note note) {
        //return noteRepository.save(note);
        return noteService.createNote(note);
    }

    @GetMapping("/notes/{id}")
    public Note getNoteById(@PathVariable(value = "id") Long noteId) {
        // return noteRepository.findById(noteId)
        // .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));
        return noteService.getNoteById(noteId);
    }

    @GetMapping("/notes")
    public List <Note> getNotesByUserId() {
        // public List <Note> getNoteByUserId(@PathVariable Long userId){
        //return noteRepository.findByUserId(userId);
        return noteService.getNotesByUserId();
        //.orElseThrow(() -> new ResourceNotFoundException("Note", "id", userId));
    }


    @PutMapping("/notes/{id}")
    public Note updateNote(@PathVariable(value = "id") Long noteId,
                           @Valid @RequestBody Note noteDetails) {

        //Note note = noteRepository.findById(noteId)
               // .orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

       // note.setTitle(noteDetails.getTitle());
       // note.setContent(noteDetails.getContent());

       // Note updatedNote = noteRepository.save(note);
       // return updatedNote;
        return noteService.updateNote(noteId,noteDetails);
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable(value = "id") Long noteId) {
        //Note note = noteRepository.findById(noteId)
        //.orElseThrow(() -> new ResourceNotFoundException("Note", "id", noteId));

        // noteRepository.delete(note);

        // return ResponseEntity.ok().build();
        return noteService.deleteNote(noteId);
    }

}
