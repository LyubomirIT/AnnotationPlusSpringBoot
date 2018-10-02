package com.nbu.annotationplus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidInputParamsException extends RuntimeException {

    public InvalidInputParamsException(String message) {
        super(message);
    }
}