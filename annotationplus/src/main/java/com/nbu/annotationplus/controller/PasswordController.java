package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.dto.DtoForgotPassword;
import com.nbu.annotationplus.dto.DtoPassword;
import com.nbu.annotationplus.service.PasswordService;
import com.nbu.annotationplus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class PasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordService;

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody DtoPassword dtoPassword) {
        return userService.updatePassword(dtoPassword);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestToken(@Valid @RequestBody DtoForgotPassword dtoForgotPassword, HttpServletRequest request) {
        return passwordService.requestToken(dtoForgotPassword, request);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody DtoPassword dtoPassword) {
        return passwordService.resetPassword(dtoPassword);
    }
}
