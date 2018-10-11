package com.nbu.annotationplus.utils;

public class ParseUtils {

    public static boolean validateTitle(String title){
        return (title.length() < 2 || title.length() > 24 || title.trim().equals(""));
    }

    public static boolean validateContent(String content){
        return (content.trim().equals(""));
    }
}