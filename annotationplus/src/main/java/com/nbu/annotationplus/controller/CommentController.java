package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.dto.DtoComment;
import com.nbu.annotationplus.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/comment")
    public ResponseEntity<DtoComment> createComment(@Valid @RequestBody DtoComment dtoComment) {
        return commentService.createComment(dtoComment);
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable(value = "id") Long id){
        return commentService.deleteComment(id);
    }

    @GetMapping("/comment/{id}")
    public List<DtoComment> getAllComments(@PathVariable(value = "id") String annotationUid) {
        return commentService.getAllComments(annotationUid);
    }
}
