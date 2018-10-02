package com.nbu.annotationplus.service;

import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.exception.UnauthorizedException;
import com.nbu.annotationplus.model.Note;
import com.nbu.annotationplus.model.User;
import com.nbu.annotationplus.repository.NoteRepository;
import com.nbu.annotationplus.utils.ParseUtils;
import com.nbu.annotationplus.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public Note createNote(Note note){
        /*User currentUser = UserUtils.getAuthenticateduser();
        if(currentUser == null){
           throw new UnauthorizedException("Unauthorized");
       }*/
        validateNote(note);
        return noteRepository.save(note);
        // ResponseEntity<>(Note, HttpStatus.OK);
    }

    @Transactional
    public List<Note> getAllNotes(){
       /* User currentUser = UserUtils.getAuthenticateduser();
        if(currentUser == null){
            throw new UnauthorizedException("Unauthorized");
        }*/
        return noteRepository.findAll();
    }

   /* @Transactional
    public List<Note> getAllNotes(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Auth"+ authentication);
        //String authentication2 = UserUtils.getAuthenticateduser(authentication);
        if(authentication == null){
            throw new UnauthorizedException("Unauthorized");
        }
        return noteRepository.findAll();
    }*/

    @Transactional
    public Note getNoteById(Long id){
        /*User currentUser = UserUtils.getAuthenticateduser();
        if(currentUser == null){
            throw new UnauthorizedException("Unauthorized");
        }*/
        return noteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Note", "id", id));
    }

    @Transactional
    public List<Note> getNoteByUserId(Long id){
       /* User currentUser = UserUtils.getAuthenticateduser();
        if(currentUser == null){
            throw new UnauthorizedException("Unauthorized");
        }*/
        return noteRepository.findByUserId(id);
    }

    @Transactional
    public ResponseEntity<?> deleteNote(Long id){
      /*  User currentUser = UserUtils.getAuthenticateduser();
        if(currentUser == null){
            throw new UnauthorizedException("Unauthorized");
        }*/
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", id));
        noteRepository.delete(note);
        return ResponseEntity.ok().build();
    }

    @Transactional
    public Note updateNote(Long id, Note noteDetails){
       /* User currentUser = UserUtils.getAuthenticateduser();
        if(currentUser == null){
            throw new UnauthorizedException("Unauthorized");
        }*/
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", id));
        validateNote(noteDetails);
        note.setTitle(noteDetails.getTitle());
        note.setContent(noteDetails.getContent());
        Note updatedNote = noteRepository.save(note);
        return updatedNote;
    }


    private void validateNote(Note note){
        if(!ParseUtils.validateTitle(note.getTitle())){
            throw new InvalidInputParamsException("Invalid Title");
        }
        if(!ParseUtils.validateContent(note.getContent())){
            throw new InvalidInputParamsException("Invalid Content");
        }
    }
}
