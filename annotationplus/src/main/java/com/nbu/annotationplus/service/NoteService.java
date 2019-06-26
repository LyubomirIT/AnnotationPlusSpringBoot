package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoNote;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.exception.UnauthorizedException;
import com.nbu.annotationplus.persistence.entity.Note;
import com.nbu.annotationplus.persistence.entity.User;
import com.nbu.annotationplus.persistence.repository.CommentRepository;
import com.nbu.annotationplus.persistence.repository.NoteRepository;
import com.nbu.annotationplus.persistence.repository.UserRepository;
import com.nbu.annotationplus.utils.AuthUtils;
import com.nbu.annotationplus.utils.ParseUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommentRepository commentRepository;

    private DtoNote toDtoNote(Note note) {
        ModelMapper modelMapper = new ModelMapper();
        DtoNote dtoNote = modelMapper.map(note, DtoNote.class);
        return dtoNote;
    }

    @Transactional
    public ResponseEntity<DtoNote> createNote(DtoNote dtoNote){
        //validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        Long userId = currentUser.getId();
        Note note = new Note();
        note.setUserId(userId);
        validateNote(dtoNote);
        categoryService.getCategoryById(dtoNote.getCategoryId());
        note.setTitle(dtoNote.getTitle().trim());
        note.setContent(dtoNote.getContent());
        note.setCategoryId(dtoNote.getCategoryId());
        Note existingNote = noteRepository.findByTitleAndUserId(note.getTitle().trim(),userId);
        if(existingNote == null){
            noteRepository.save(note);
            return new ResponseEntity<DtoNote>(toDtoNote(note), HttpStatus.CREATED);
        }else{
            throw new InvalidInputParamsException("Note with name: " + "'" + dtoNote.getTitle() + "'" + " already exists.");
        }
    }

    @Transactional
    public DtoNote getNoteById(Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        Long userId = currentUser.getId();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", id));
        Long noteUserId = note.getUserId();
        if(userId.equals(noteUserId)){
            return toDtoNote(note);
        }else{
            throw new ResourceNotFoundException("Note", "id", id);
        }
    }

    @Transactional
    public List<DtoNote> getNotesByCategoryIdAndUserId(Long categoryId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        Long userId = currentUser.getId();
        List<DtoNote> list;
        list = new ArrayList<>();
        List<Note> noteList = noteRepository.findByCategoryIdAndUserId(categoryId, userId);
        for(Note note: noteList){
            list.add(toDtoNote(note));
        }
        return list;
    }

    @Transactional
    public List<DtoNote> getNotesByUserId(){
        //validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        Long userId = currentUser.getId();
        List<DtoNote> list;
        list = new ArrayList<>();
        List<Note> noteList = noteRepository.findByUserId(userId);
        for(Note note: noteList){
            list.add(toDtoNote(note));
        }
        return list;
    }

    @Transactional
    public ResponseEntity<?> deleteNote(Long id){
        //validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        Long userId = currentUser.getId();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", id));
        Long noteUserId = note.getUserId();
        if(userId.equals(noteUserId)){
            noteRepository.delete(note);
            commentRepository.deleteAllByNoteId(id);
            return ResponseEntity.ok().build();
        }else{
            throw new ResourceNotFoundException("Note", "id", id);
        }
    }

    @Transactional
    public DtoNote updateNote(Long id, DtoNote dtoNote){
        //validateUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail);
        Long userId = currentUser.getId();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", id));
        Long noteUserId = note.getUserId();
        if(userId.equals(noteUserId)){
            //validateNote(dtoNote);
            if(dtoNote.getTitle() == null){
                dtoNote.setTitle(note.getTitle());
            }
            Note existingNote = noteRepository.findByTitleAndUserId(dtoNote.getTitle().trim(),userId);
            if(existingNote == null || existingNote.getId().equals(id)){
                //note.setUserId(note.getUserId());
                if(dtoNote.getCategoryId() == null){
                    dtoNote.setCategoryId(note.getCategoryId());
                }
               /* if(dtoNote.getTitle() == null){
                    dtoNote.setTitle(note.getTitle());
                }*/
                if(dtoNote.getContent() == null){
                    dtoNote.setContent(note.getContent());
                }
                note.setTitle(dtoNote.getTitle().trim());
                note.setContent(dtoNote.getContent());
                categoryService.getCategoryById(dtoNote.getCategoryId());
                note.setCategoryId(dtoNote.getCategoryId());
                note.setUpdatedTs(LocalDateTime.now(Clock.systemUTC()));
                noteRepository.save(note);
                return toDtoNote(note);
            }else {
                throw new InvalidInputParamsException("Note with name: " + "'" + dtoNote.getTitle() + "'" + " already exists.");
            }
        }else {
            throw new ResourceNotFoundException("Note", "id", id);
        }
    }

    private void validateNote(DtoNote dtoNote){
        /*if(ParseUtils.validateTitle(dtoNote.getTitle())){
            throw new InvalidInputParamsException("Invalid Title");
        }*/
        if(ParseUtils.validateContent(dtoNote.getContent())){
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
