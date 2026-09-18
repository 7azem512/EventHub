package com.eventhub.media.exception;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MediaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMediaNotFoundException(MediaNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("timestamp", LocalDateTime.now(),
                        "status",404,
                        "error","Not Found",
                        "message",ex.getMessage(),
                        "path",request.getRequestURI()));
    }

    @ExceptionHandler(InvalidMediaFileException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidMediaFile(InvalidMediaFileException ex, HttpServletRequest request)
    {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 400,
                        "error", "Bad Request",
                        "message", ex.getMessage(),
                        "path", request.getRequestURI()
                ));
    }

    @ExceptionHandler(MediaStorageException.class)
    public ResponseEntity<Map<String, Object>> handleMediaStorage(MediaStorageException ex, HttpServletRequest request)
    {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 500,
                        "error", "Storage Error",
                        "message", ex.getMessage(),
                        "path", request.getRequestURI()
                ));
    }

    @ExceptionHandler(MediaAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleMediaAccessDenied(
            MediaAccessDeniedException ex,
            HttpServletRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 403,
                        "error", "Forbidden",
                        "message", ex.getMessage(),
                        "path", request.getRequestURI()
                ));
    }
}
