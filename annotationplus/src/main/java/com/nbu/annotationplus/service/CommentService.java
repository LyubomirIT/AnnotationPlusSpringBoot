package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoComment;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.persistence.entity.Comment;
import com.nbu.annotationplus.persistence.repository.CommentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AnnotationService annotationService;

    private DtoComment toDtoComment(Comment comment) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(comment, DtoComment.class);
    }

    @Transactional
    public List<DtoComment> getAllComments(Long annotationId) {
        Long currentUserId = userService.getUserId();
        List<DtoComment> list;
        list = new ArrayList<>();
        List<Comment> commentList;
        if(annotationId == null){
            commentList = commentRepository.findByUserIdOrderByCreatedTsDesc(currentUserId);
            for(Comment comment: commentList){
                list.add(toDtoComment(comment));
            }
            return list;
        }else {
            commentList = commentRepository.findByAnnotationIdAndUserIdOrderByCreatedTsDesc(annotationId, currentUserId);
            for (Comment comment : commentList) {
                list.add(toDtoComment(comment));
            }
            return list;
        }
    }

    @Transactional
    public ResponseEntity<DtoComment> createComment(DtoComment dtoComment){
        String userName = userService.getCurrentUser().getEmail();
        Long currentUserId = userService.getUserId();
        if(dtoComment.getAnnotationId() == null ){
            throw new InvalidInputParamsException("Annotation Id is required");
        }
        annotationService.getAnnotationById(dtoComment.getAnnotationId());
        Comment comment = new Comment();
        comment.setUserId(currentUserId);
        comment.setUserName(userName);
        if(dtoComment.getComment() == null || dtoComment.getComment().trim().equals("")){
            throw new InvalidInputParamsException("Comment cannot be empty");
        }
        comment.setComment(dtoComment.getComment());
        comment.setAnnotationId(dtoComment.getAnnotationId());
        commentRepository.save(comment);
        return new ResponseEntity<DtoComment>(toDtoComment(comment), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> deleteCommentById(Long id){
        Long currentUserId = userService.getUserId();
        Comment comment = commentRepository.findByIdAndUserId(id,currentUserId);
        if(comment == null){
                throw new ResourceNotFoundException("Comment", "id",id);
        }
        commentRepository.deleteByIdAndUserId(id,currentUserId);
        return ResponseEntity.ok().build();
    }
}
