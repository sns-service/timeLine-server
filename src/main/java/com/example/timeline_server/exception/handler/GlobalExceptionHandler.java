package com.example.timeline_server.exception.handler;

import com.example.timeline_server.exception.BaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handleBaseException(BaseException exception) {
        System.out.println("Handling BaseException - " + exception.getMessage());
        return ResponseEntity
                .status(exception.getCode())
                .body(exception.getMessage());
    }
}
