package com.nbu.annotationplus.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtils {

    private static final int NAME_MAX_LENGTH = 50;
    private static final int NAME_MIN_LENGTH = 1;
    public static final int MAX_FILE_SIZE_IN_BYTES = 5242880; // 5MB
    public static final String INVALID_PASSWORD_ERROR = "Password must be between " + NAME_MIN_LENGTH +  " and " + NAME_MAX_LENGTH + " symbols!";
    public static final String PASSWORDS_NOT_THE_SAME_ERROR = "New Password must have the same value as Confirm Password!";
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_NAME_REGEX =
            Pattern.compile("[^a-zA-Z ]", Pattern.CASE_INSENSITIVE);
   // private static final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$",Pattern.);

    public static boolean validateTitle(String title){
        if (title == null){
            return false;
        }
        Matcher matcher = VALID_NAME_REGEX.matcher(title);
        return (matcher.find() || title.length() < NAME_MIN_LENGTH || title.length() > NAME_MAX_LENGTH || title.trim().equals(""));
    }

    public static boolean validateContent(String content){
        return (content == null || content.trim().equals(""));
    }

    public static boolean validateEmail(String emailStr) {
        if(emailStr == null){
            return false;
        }
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean validatePassword(String password){
        return (password == null || password.length() < NAME_MIN_LENGTH || password.length() > NAME_MAX_LENGTH || password.trim().equals(""));
    }
}