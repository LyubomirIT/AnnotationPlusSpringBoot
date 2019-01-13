package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.model.User;
import com.nbu.annotationplus.model.userDto;
import com.nbu.annotationplus.repository.UserRepository;
import com.nbu.annotationplus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<User> getAllUsers(){return userRepository.findAll();}

    @GetMapping("/user")
    public User getCurrentUser(){
        return userService.getCurrentUser();
    }

    //@PutMapping("/user")
    //public User updateUser(@Valid @RequestBody User userDetails) {
       //return userService.updateUser(userDetails);
    //}

    @PutMapping("/user")
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody userDto userDto) {
        return userService.updatePassword(userDto);
    }

   /* @GetMapping("/users/{id}")
    public User getUserById(@PathVariable(value = "id") int userId) {
        return userRepository.findById(userId);
                //.orElseThrow(() -> new ResourceNotFoundException("Note", "id", userId));
    }*/
}
