package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoFeedback;
import com.nbu.annotationplus.persistence.entity.Feedback;
import com.nbu.annotationplus.persistence.repository.FeedbackRepository;
import com.nbu.annotationplus.persistence.repository.UserRepository;
import com.nbu.annotationplus.utils.Component;
import com.nbu.annotationplus.utils.Type;
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
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    private DtoFeedback toDtoFeedback(Feedback feedback) {
        ModelMapper modelMapper = new ModelMapper();
        DtoFeedback dtoFeedback = modelMapper.map(feedback, DtoFeedback.class);
        return dtoFeedback;
    }

    @Transactional
    public ResponseEntity<DtoFeedback> createFeedback(DtoFeedback dtoFeedback){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        //User user = userRepository.findByEmail(userEmail);
        //String userName = user.getName() + " " + user.getLastName();
        Feedback feedback = new Feedback();

        feedback.setEmail(userEmail);
        feedback.setName(dtoFeedback.getName());
        feedback.setMessage(dtoFeedback.getMessage());
        Type.getByName(dtoFeedback.getType());
        Component.getByName(dtoFeedback.getComponent());
        feedback.setType(dtoFeedback.getType());
        feedback.setComponent(dtoFeedback.getComponent());

        feedback = feedbackRepository.save(feedback);
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
}
