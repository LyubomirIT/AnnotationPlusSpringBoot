package com.nbu.annotationplus.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    public static Authentication getAuthenticateduser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //System.out.println("User:" + authentication );
        //System.out.println("currentUser:" +authentication );
        return authentication;
    }
}
