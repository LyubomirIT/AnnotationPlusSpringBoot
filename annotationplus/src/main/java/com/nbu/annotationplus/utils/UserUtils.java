package com.nbu.annotationplus.utils;

import com.nbu.annotationplus.exception.InvalidInputParamsException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserUtils {

    private static final int MAX_USER_NAME_LENGTH = 25;
    private static final int MAX_PASSWORD_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 5;
    private static final String INVALID_PASSWORD_ERROR = "Password must be between " + MIN_PASSWORD_LENGTH +  " and " + MAX_PASSWORD_LENGTH + " symbols!";
    public static final String PASSWORDS_NOT_THE_SAME_ERROR = "New Password must have the same value as Confirm Password!";
    private static final Pattern VALID_USER_NAME_REGEX =
            Pattern.compile("[^a-zA-Z ]", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static void validateFirstAndLastUserName(String firstName,String lastName) {
        if (firstName == null || firstName.trim().equals("")) {
            throw new InvalidInputParamsException("First name is required");
        }
        if(firstName.length() > MAX_USER_NAME_LENGTH ){
            throw new InvalidInputParamsException("First name should NOT be more than " + MAX_USER_NAME_LENGTH + " characters long");
        }
        if (lastName == null || lastName.trim().equals("")) {
            throw new InvalidInputParamsException("Last name is required");
        }
        if(lastName.length() > MAX_USER_NAME_LENGTH ){
            throw new InvalidInputParamsException("Last name should NOT be more than " + MAX_USER_NAME_LENGTH + " characters long");
        }
        Matcher matcher = VALID_USER_NAME_REGEX.matcher(firstName);
        if (matcher.find()){
            throw new InvalidInputParamsException("First name cannot contain any special characters");
        }
        matcher = VALID_USER_NAME_REGEX.matcher(lastName);
        if (matcher.find()){
            throw new InvalidInputParamsException("Last name cannot contain any special characters");
        }
    }

    public static void validateEmail(String emailStr) {
        if(emailStr == null || emailStr.trim().equals("")){
            throw new InvalidInputParamsException("Email Address is required");
        }
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        if (!matcher.find()){
            throw new InvalidInputParamsException("Provided email address is is not valid");
        }
    }

    public static void validatePassword(String password){
        if (password == null || password.trim().equals("")){
            throw new InvalidInputParamsException("Password Is Required");
        }

        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH){
            throw new InvalidInputParamsException(INVALID_PASSWORD_ERROR);
        }
    }
}
