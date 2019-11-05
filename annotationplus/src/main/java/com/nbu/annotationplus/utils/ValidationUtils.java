package com.nbu.annotationplus.utils;

import com.nbu.annotationplus.exception.InvalidInputParamsException;

public class ValidationUtils {

    public static final int NAME_MAX_LENGTH = 50;

    public static void validateName(String nameValue) {
        if(nameValue == null || nameValue.trim().equals("")){
            throw new InvalidInputParamsException("Name cannot be empty");
        }
        validateFieldLength(nameValue, "Name", NAME_MAX_LENGTH);
    }

    public static void validateFieldLength(String fieldValue, String fieldName, int maxLength) {
        if (fieldValue.length() > NAME_MAX_LENGTH) {
            throw new InvalidInputParamsException(fieldName + " should NOT be more than " + maxLength + " characters.");
        }
    }
}
