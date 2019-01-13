package com.nbu.annotationplus.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class InternalExceptionsHandler extends ResponseEntityExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(InternalExceptionsHandler.class);

    /**
     * Handle JSON parse exceptions and customize the response for HttpMessageNotReadableException.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.error("{} : {}", ex.getClass().getName(), ex.getMessage());

        String error = "JSON parse exception: Cannot construct instance from the request body.";
        return buildResponseEntity(new ApiError(status, error));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
