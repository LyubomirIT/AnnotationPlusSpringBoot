package com.nbu.annotationplus.utils;

import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class ParseUtils {

    public static boolean validateTitle(String title){

        if (title.length() < 2 || title.length() > 24 || title.trim().equals("")){
            return false;
        }
        else{
            return true;
        }
    }

    public static boolean validateContent(String content){

        if (content.trim().equals("")){
            return false;
        }
        else{
            return true;
        }
    }
}