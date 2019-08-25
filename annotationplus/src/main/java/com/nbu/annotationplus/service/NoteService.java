package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoNote;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.persistence.entity.Category;
import com.nbu.annotationplus.persistence.entity.Note;
import com.nbu.annotationplus.persistence.repository.*;
import com.nbu.annotationplus.utils.ParseUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    private DtoNote toDtoNote(Note note) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(note, DtoNote.class);
    }

    @Transactional
    public ResponseEntity<DtoNote> createNote(DtoNote dtoNote){
        Long currentUserId = userService.getUserId();
        validateNote(dtoNote);
        if(dtoNote.getCategory() == null){
            throw new InvalidInputParamsException("Category is required!");
        }
        if(dtoNote.getCategory().getId() == null){
            throw new InvalidInputParamsException("Category id is required!");
        }
        Category category = categoryRepository.findByIdAndUserId(dtoNote.getCategory().getId(),currentUserId);
        if(category == null){
            throw new ResourceNotFoundException("Category", "id", dtoNote.getCategory().getId());
        }
        if(!noteRepository.findByTitleAndUserId(dtoNote.getTitle().trim(),currentUserId).isPresent()){
            Note note = new Note();
            note.setUserId(currentUserId);
            note.setTitle(dtoNote.getTitle().trim());
            note.setContent(dtoNote.getContent());
            note.setCategory(category);
            noteRepository.save(note);
            return new ResponseEntity<DtoNote>(toDtoNote(note), HttpStatus.CREATED);
        }else{
            throw new InvalidInputParamsException("Note with name: " + "'" + dtoNote.getTitle() + "'" + " already exists.");
        }
    }

    @Transactional
    public DtoNote getNoteById(Long id){
        Long currentUserId = userService.getUserId();
        Note note = noteRepository.findByIdAndUserId(id,currentUserId);
        if(note == null){
            throw new ResourceNotFoundException("Note", "id", id);
        }
        else{
            return toDtoNote(note);
        }
    }

    @Transactional
    public List<DtoNote> getNotesByCategoryIdAndUserId(Long categoryId){
        Long currentUserId = userService.getUserId();
        List<DtoNote> list;
        list = new ArrayList<>();
        List<Note> noteList = noteRepository.findByCategoryIdAndUserId(categoryId, currentUserId);
        for(Note note: noteList){
            list.add(toDtoNote(note));
        }
        return list;
    }

    @Transactional
    public List<DtoNote> getNotesByUserId(){
        Long currentUserId = userService.getUserId();
        List<DtoNote> list;
        list = new ArrayList<>();
        List<Note> noteList = noteRepository.findByUserId(currentUserId);
        for(Note note: noteList){
            list.add(toDtoNote(note));
        }
        return list;
    }

    @Transactional
    public ResponseEntity<?> deleteNote(Long id){
        Long currentUserId = userService.getUserId();
        Note note = noteRepository.findByIdAndUserId(id, currentUserId);
        if(note == null){
            throw new ResourceNotFoundException("Note", "id", id);
        }
        else{
            noteRepository.delete(note);
            return ResponseEntity.ok().build();
        }
    }

    @Transactional
    public DtoNote updateNote(Long id, DtoNote dtoNote){
        Long currentUserId = userService.getUserId();
        Note note = noteRepository.findByIdAndUserId(id,currentUserId);
        if(note == null){
            throw new ResourceNotFoundException("Note", "id", id);
        }
        if(dtoNote.getTitle() != null && !dtoNote.getTitle().trim().equals("")){
            if(!note.getTitle().equals(dtoNote.getTitle().trim())){
                ParseUtils.validateTitle(dtoNote.getTitle().trim());
                if(noteRepository.findByTitleAndUserId(dtoNote.getTitle().trim(),currentUserId).isPresent()){
                    throw new InvalidInputParamsException("Note with name: " + "'" + dtoNote.getTitle() + "'" + " already exists.");
                }
                note.setTitle(dtoNote.getTitle().trim());
            }
        }
        if(dtoNote.getCategory() != null){
            if(!note.getCategory().getId().equals(dtoNote.getCategory().getId())){
                Category category = categoryRepository.findByIdAndUserId(dtoNote.getCategory().getId(),currentUserId);
                if(category == null){
                    throw new ResourceNotFoundException("Category", "id", dtoNote.getCategory().getId());
                }
                note.setCategory(dtoNote.getCategory());
            }
        }
        if(dtoNote.getContent() != null && !dtoNote.getContent().trim().equals("")){
            if(!note.getContent().equals(dtoNote.getContent())){
                note.setContent(dtoNote.getContent());
            }
        }

        note.setUpdatedTs(LocalDateTime.now(Clock.systemUTC()));
        noteRepository.save(note);
        return toDtoNote(note);
    }

    private void validateNote(DtoNote dtoNote){
        if(ParseUtils.validateTitle(dtoNote.getTitle())){
            throw new InvalidInputParamsException("Invalid Title");
        }
        if(ParseUtils.validateContent(dtoNote.getContent())){
            throw new InvalidInputParamsException("Invalid Content");
        }
    }
}
