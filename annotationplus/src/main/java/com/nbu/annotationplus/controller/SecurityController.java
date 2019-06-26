package com.nbu.annotationplus.controller;

import java.security.Principal;
import java.util.List;

import com.nbu.annotationplus.exception.ResourceNotFoundException;

import com.nbu.annotationplus.persistence.entity.User;
import com.nbu.annotationplus.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
//@RestController
//@RequestMapping("/api")
public class SecurityController {

    //User user  = new User();

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public User currentUserName() {
     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       String email = authentication.getName();
        System.out.println(authentication);
        //System.out.println(getSignedUpUser());
        return userRepository.findByEmail(email);

    }

   /* public static int getSignedUpUser() {
        final SecurityContext ctx = SecurityContextHolder.getContext();

        if (ctx == null) {
            //LOGGER.debug("No security context available");
            return 0;
        }

        final Authentication auth = ctx.getAuthentication();
        if (auth == null) {
            //LOGGER.debug("No authentication available in security context {}", ctx);
            return 0;
        }

        final Object principal = auth.getPrincipal();
        if (!(principal instanceof User)) {
           // LOGGER.warn("Principal {} is not an instance of AUser!", principal);
            return 0;
        }

        final User au = (User)principal;
        return au.getId();
    }*/


  /*  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = (User)authentication.getPrincipal();
    int userId = user.getId();*/



    /*@GetMapping("/users")
    public List <User> getAllUsers() {
        return (userRepository.findAll());
    }*/

   /* @GetMapping("/user")
    public User getUser() {
        return (userRepository.findByEmail("email"));
    }*/


}
