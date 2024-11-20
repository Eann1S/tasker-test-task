package com.example.exceptions;

import com.example.dtos.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDto> handleUnauthorizedException(UnauthorizedException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({AuthorizationDeniedException.class, ForbiddenException.class})
    public ResponseEntity<ErrorDto> handleForbiddenException(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorDto> handleConflictException(ConflictException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleAllExceptions(Exception e) {
        log.error(e.getMessage(), e);
        ErrorDto errorDto = new ErrorDto("Something went wrong...");
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
