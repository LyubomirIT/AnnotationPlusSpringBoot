package com.nbu.annotationplus.controller;


import com.nbu.annotationplus.exception.ResourceNotFoundException;
import com.nbu.annotationplus.model.User;
import com.nbu.annotationplus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAllUsers(){return userRepository.findAll();}

    @PutMapping("/users/{id}")
    public User updateNote(@PathVariable(value = "id") int userId,
                           @Valid @RequestBody User userDetails) {

        User user = userRepository.findById(userId);
                //.orElseThrow(() -> new ResourceNotFoundException("Note", "id", userId));

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setLastName(userDetails.getLastName());

        User updatedUser = userRepository.save(user);
        return updatedUser;
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable(value = "id") int userId) {
        return userRepository.findById(userId);
                //.orElseThrow(() -> new ResourceNotFoundException("Note", "id", userId));
    }
}
