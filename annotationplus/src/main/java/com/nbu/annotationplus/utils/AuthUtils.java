package com.nbu.annotationplus.utils;

import com.nbu.annotationplus.persistence.entity.User;
import com.nbu.annotationplus.persistence.repository.UserRepository;
import com.nbu.annotationplus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

public class AuthUtils {

    @Autowired
    private static UserService userService;

    public static Authentication getAuthenticateduser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //System.out.println("User:" + authentication );
        //System.out.println("currentUser:" +authentication );
        return authentication;
    }

    public static Long getUserId(){
        return userService.getUserId();
    }




}
