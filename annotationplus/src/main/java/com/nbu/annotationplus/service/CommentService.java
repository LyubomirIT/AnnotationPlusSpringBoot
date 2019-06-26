package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoComment;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.persistence.entity.Comment;
import com.nbu.annotationplus.persistence.entity.User;
import com.nbu.annotationplus.persistence.repository.CommentRepository;
import com.nbu.annotationplus.persistence.repository.NoteRepository;
import com.nbu.annotationplus.persistence.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteService noteService;

    private DtoComment toDtoComment(Comment comment) {
        ModelMapper modelMapper = new ModelMapper();
        DtoComment dtoComment = modelMapper.map(comment, DtoComment.class);
        return dtoComment;
    }

    @Transactional
    public List<DtoComment> getAllComments(String annotationId) {
        List<DtoComment> list;
        list = new ArrayList<>();
        List<Comment> commentList = commentRepository.findByAnnotationIdOrderByCreatedTsDesc(annotationId);
        for(Comment comment: commentList){
            list.add(toDtoComment(comment));
        }
        return list;
    }

    @Transactional
    public ResponseEntity<DtoComment> createComment(DtoComment dtoComment){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        Comment comment = new Comment();
        noteService.getNoteById(dtoComment.getNoteId());
        comment.setUserId(userId);
        comment.setUserName(userName);
        comment.setNoteId(dtoComment.getNoteId());
        comment.setComment(dtoComment.getComment());
        comment.setAnnotationId(dtoComment.getAnnotationId());
        commentRepository.save(comment);
        return new ResponseEntity<DtoComment>(toDtoComment(comment), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> deleteComment(Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByEmail(userName);
        Long userId = user.getId();
        Comment comment = commentRepository.findByIdAndUserId(id,userId);
        if(comment == null){
                throw new ResourceNotFoundException("Comment", "id",id);
        }
        commentRepository.deleteByIdAndUserId(id,userId);
        return ResponseEntity.ok().build();
    }
}
