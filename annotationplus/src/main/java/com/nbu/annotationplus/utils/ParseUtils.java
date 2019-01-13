package com.nbu.annotationplus.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtils {

    public static boolean validateTitle(String title){
        return (title == null || title.length() < 2 || title.length() > 24 || title.trim().equals(""));
    }

    public static boolean validateContent(String content){
        return (content == null || content.trim().equals(""));
    }

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String emailStr) {
        if(emailStr == null){
            return false;
        }
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean validatePassword(String password){
        return (password == null || password.length() < 6 || password.length() > 40 || password.trim().equals(""));
    }

}