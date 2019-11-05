package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoFeedback;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.persistence.entity.Feedback;
import com.nbu.annotationplus.persistence.repository.FeedbackRepository;
import com.nbu.annotationplus.utils.Component;
import com.nbu.annotationplus.utils.Type;
import com.nbu.annotationplus.utils.ValidationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserService userService;

    private DtoFeedback toDtoFeedback(Feedback feedback) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(feedback, DtoFeedback.class);
    }

    @Transactional
    public ResponseEntity<DtoFeedback> createFeedback(DtoFeedback dtoFeedback){
        String userEmail = userService.getCurrentUser().getEmail();
        Feedback feedback = new Feedback();
        feedback.setEmail(userEmail);
        validateFeedbackName(dtoFeedback.getName());
        feedback.setName(dtoFeedback.getName());
        validateFeedBackMessage(dtoFeedback.getMessage());
        feedback.setMessage(dtoFeedback.getMessage());
        try {
            feedback.setType(Type.valueOf(dtoFeedback.getType().toUpperCase()));
        } catch (Exception e){
            throw new InvalidInputParamsException(dtoFeedback.getType() != null && !dtoFeedback.getType().trim().equals("") ? "The provided type: " +'"'+ dtoFeedback.getType() +'"'+ " does not exist." : "Type is required.");
        }
        try {
            feedback.setComponent(Component.valueOf(dtoFeedback.getComponent().toUpperCase()));
        } catch (Exception e){
            throw new InvalidInputParamsException(dtoFeedback.getComponent() != null && !dtoFeedback.getComponent().trim().equals("") ? "The provided component: " +'"'+ dtoFeedback.getComponent() +'"'+ " does not exist." : "Component is required.");
        }
        feedbackRepository.save(feedback);
        return new ResponseEntity<DtoFeedback>(HttpStatus.CREATED);
    }

    @Transactional
    public List<DtoFeedback> getAllFeedbacks(){
        List<DtoFeedback> list;
        list = new ArrayList<>();
        List<Feedback> feedbackList = feedbackRepository.findAll();
        for(Feedback feedback: feedbackList){
            list.add(toDtoFeedback(feedback));
        }
        return list;
    }

    private void validateFeedbackName(String feedbackName){
        ValidationUtils.validateName(feedbackName);
    }
     private void validateFeedBackMessage(String feedbackMessage){
        if(feedbackMessage == null || feedbackMessage.trim().equals("")){
            throw new InvalidInputParamsException("Message cannot be empty");
        }
     }
}
