package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.dto.DtoUser;
import com.nbu.annotationplus.persistence.entity.User;
import com.nbu.annotationplus.persistence.repository.UserRepository;
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
    public DtoUser getCurrentUser(){
        return userService.getCurrentUser();
    }

    @PutMapping("/user")
    public ResponseEntity<DtoUser> updateCurrentUser(@Valid @RequestBody DtoUser dtoUser) {
        return userService.updateCurrentUser(dtoUser);
    }

    @PostMapping("/user")
    public ResponseEntity<DtoUser> createUser(@Valid @RequestBody DtoUser dtoUser) {
        return userService.createUser(dtoUser);
    }
}
