package Medco.PremuimFinancing.exception;


import Medco.PremuimFinancing.Dto.Response.ResponseMessage;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(fieldName, message);
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseMessage> BadRequestExceptionHandler(BadRequestException ex, WebRequest request) {
        ResponseMessage message = new ResponseMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage());

        return new ResponseEntity<ResponseMessage>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNameNotFoundException.class)
    public ResponseEntity<ResponseMessage> UserNameNotFoundExceptionHandler(UserNameNotFoundException ex, WebRequest request) {
        ResponseMessage message = new ResponseMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage());

        return new ResponseEntity<ResponseMessage>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ResponseMessage> InvalidCredentialHandler(InvalidCredentialsException ex, WebRequest request) {
        ResponseMessage message = new ResponseMessage(
                HttpStatus.UNAUTHORIZED.value(),
                new Date(),
                ex.getMessage());

        return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseMessage> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        String message = "Operation failed due to data constraint violation";
        
        // Check for unique constraint violations
        String exceptionMessage = ex.getMessage();
        if (exceptionMessage != null) {
            exceptionMessage = exceptionMessage.toLowerCase();
            if (exceptionMessage.contains("uk_") || exceptionMessage.contains("unique") || exceptionMessage.contains("duplicate")) {
                message = "Institution name already exists. Please use a different name.";
            } else if (exceptionMessage.contains("not-null") || exceptionMessage.contains("not null")) {
                message = "Required field is missing. Please provide all required fields.";
            } else if (exceptionMessage.contains("foreign key")) {
                message = "Invalid reference. The referenced record does not exist.";
            }
        }
        
        ResponseMessage responseMessage = new ResponseMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                message);

        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public ResponseEntity<ResponseMessage> handleHibernateConstraintViolation(
            org.hibernate.exception.ConstraintViolationException ex, WebRequest request) {
        String message = "Operation failed due to data constraint violation";
        
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("unique")) {
                message = "Institution name already exists. Please use a different name.";
            }
        }
        
        ResponseMessage responseMessage = new ResponseMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                message);

        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseMessage> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        ResponseMessage message = new ResponseMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage());

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        ResponseMessage message = new ResponseMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage());

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ResponseMessage> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ResponseMessage message = new ResponseMessage(
                status.value(),
                new Date(),
                ex.getReason() != null ? ex.getReason() : status.getReasonPhrase());
        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseMessage> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        ResponseMessage message = new ResponseMessage(
                HttpStatus.FORBIDDEN.value(),
                new Date(),
                ex.getMessage());
        return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage> globalExceptionHandler(Exception ex, WebRequest request) {
        String message = ex.getMessage();
        
        // Provide user-friendly message for file upload errors
        if (message != null && message.contains("Failed to process")) {
            message = "Failed to process the uploaded image. Please ensure it's a valid image file (PNG, JPEG, JPG, GIF, or WEBP).";
        }
        
        ResponseMessage responseMessage = new ResponseMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                message != null ? message : "An unexpected error occurred. Please try again later.");

        return new ResponseEntity<>(responseMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}



//package com.medcoAnalytics.invoiceService.exception;
//
//import com.medcoAnalytics.invoiceService.dto.Response.ResponseMessage;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//
//import java.util.Date;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<ResponseMessage> BadRequestExceptionHandler(BadRequestException ex, WebRequest request) {
//        ResponseMessage message = new ResponseMessage(
//                HttpStatus.NOT_FOUND.value(),
//                new Date(),
//                ex.getMessage());
//
//        return new ResponseEntity<ResponseMessage>(message, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(UserNameNotFoundException.class)
//    public ResponseEntity<ResponseMessage> UserNameNotFoundExceptionHandler(UserNameNotFoundException ex, WebRequest request) {
//        ResponseMessage message = new ResponseMessage(
//                HttpStatus.NOT_FOUND.value(),
//                new Date(),
//                ex.getMessage());
//
//        return new ResponseEntity<ResponseMessage>(message, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(InvalidCredentialsException.class)
//    public ResponseEntity<ResponseMessage> InvalidCredentialHandler(InvalidCredentialsException ex, WebRequest request) {
//        ResponseMessage message = new ResponseMessage(
//                HttpStatus.UNAUTHORIZED.value(),
//                new Date(),
//                ex.getMessage());
//
//        return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ResponseMessage> globalExceptionHandler(Exception ex, WebRequest request) {
//        ResponseMessage message = new ResponseMessage(
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                new Date(),
//                ex.getMessage());
//
//        return new ResponseEntity<ResponseMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);


