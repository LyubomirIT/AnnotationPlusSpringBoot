package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.dto.DtoFeedback;
import com.nbu.annotationplus.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/feedback")
    public ResponseEntity<DtoFeedback> createFeedback(@Valid @RequestBody DtoFeedback dtoFeedback) {
        return feedbackService.createFeedback(dtoFeedback);
    }

    @GetMapping("/feedback")
    public List<DtoFeedback> getAllFeedbacks(){return feedbackService.getAllFeedbacks();}
}
