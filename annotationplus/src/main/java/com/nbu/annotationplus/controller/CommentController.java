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
    public ResponseEntity<?> deleteCommentById(@PathVariable(value = "id") Long id){
        return commentService.deleteCommentById(id);
    }

    @GetMapping("/comment")
    public List<DtoComment> getAllComments(@RequestParam(required = false) Long annotationId) {
        return commentService.getAllComments(annotationId);
    }
}
