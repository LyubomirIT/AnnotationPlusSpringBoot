package com.nbu.annotationplus.service;

import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.exception.UnauthorizedException;
import com.nbu.annotationplus.model.Note;
import com.nbu.annotationplus.model.User;
import com.nbu.annotationplus.repository.NoteRepository;
import com.nbu.annotationplus.repository.UserRepository;
import com.nbu.annotationplus.utils.AuthUtils;
import com.nbu.annotationplus.utils.ParseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryService categoryService;

    @Transactional
    public ResponseEntity<Note> createNote(Note note){
        validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        Long userId = currentUser.getId();
        note.setUserId(userId);
        validateNote(note);
        categoryService.getCategoryById(note.getCategoryId());
        Note existingNote = noteRepository.findByTitleAndUserId(note.getTitle(),userId);
        if(existingNote == null){
            noteRepository.save(note);
            return new ResponseEntity<Note>(note, HttpStatus.CREATED);
        }else{
            throw new InvalidInputParamsException("Note with name: " + "'" + note.getTitle() + "'" + " already exists.");
        }
    }

    @Transactional
    public Note getNoteById(Long id){
        validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        Long userId = currentUser.getId();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", id));
        Long noteUserId = note.getUserId();
        if(userId.equals(noteUserId)){
            return note;
        }else{
            throw new ResourceNotFoundException("Note", "id", id);
        }
    }

    @Transactional
    public List<Note> getNotesByUserId(){
        validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        Long userId = currentUser.getId();
        return noteRepository.findByUserId(userId);
    }

    @Transactional
    public ResponseEntity<?> deleteNote(Long id){
        validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        Long userId = currentUser.getId();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", id));
        Long noteUserId = note.getUserId();
        if(userId.equals(noteUserId)){
            noteRepository.delete(note);
            return ResponseEntity.ok().build();
        }else{
            throw new ResourceNotFoundException("Note", "id", id);
        }
    }

    @Transactional
    public Note updateNote(Long id, Note noteDetails){
        validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        Long userId = currentUser.getId();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", id));
        Long noteUserId = note.getUserId();
        if(userId.equals(noteUserId)){
            validateNote(noteDetails);
            Note existingNote = noteRepository.findByTitleAndUserId(noteDetails.getTitle(),userId);
            if(existingNote == null || existingNote.getId().equals(id)){
                note.setUserId(note.getUserId());
                note.setTitle(noteDetails.getTitle());
                note.setContent(noteDetails.getContent());
                categoryService.getCategoryById(noteDetails.getCategoryId());
                note.setCategoryId(noteDetails.getCategoryId());
                return noteRepository.save(note);
            }else {
                throw new InvalidInputParamsException("Note with name: " + "'" + noteDetails.getTitle() + "'" + " already exists.");
            }
        }else {
            throw new ResourceNotFoundException("Note", "id", id);
        }
    }

    private void validateNote(Note note){
        if(ParseUtils.validateTitle(note.getTitle())){
            throw new InvalidInputParamsException("Invalid Title");
        }
        if(ParseUtils.validateContent(note.getContent())){
            throw new InvalidInputParamsException("Invalid Content");
        }
    }

    private void validateUser(){
        Authentication authentication = AuthUtils.getAuthenticateduser();
        if(authentication == null){
            throw new UnauthorizedException("Unauthorized");
        }
    }
}
