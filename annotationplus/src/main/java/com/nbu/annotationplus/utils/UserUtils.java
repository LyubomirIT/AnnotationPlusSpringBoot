package com.nbu.annotationplus.utils;

import com.nbu.annotationplus.model.User;
import com.nbu.annotationplus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.nbu.annotationplus.controller.SecurityController.getSignedUpUser;




public class UserUtils {

    @Autowired
    private UserRepository userRepository;

    /*public static User getAuthenticateduser(){
        Object user = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User:" + user );
        //if (user == null){
        // throw new InvalidInputParamsException("test");
        // }
        User currentUser = (user instanceof User) ? (User) user : null;
        System.out.println("currentUser:" + currentUser );
        return currentUser;
    }*/

    public static String getAuthenticateduser(Authentication authentication){
         authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User:" + authentication);

        String email = authentication.getName();
       // System.out.println("USerID");
        //System.out.println(getSignedUpUser());
       // userRepository.findByEmail(email);
        //if (user == null){
        // throw new InvalidInputParamsException("test");
        // }
        //User currentUser = (user instanceof User) ? (User) user : null;
        System.out.println("currentUser:" + email );
        return email;
    }
}
