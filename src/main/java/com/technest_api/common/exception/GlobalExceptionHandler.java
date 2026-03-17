package com.technest_api.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException exception, HttpServletRequest request) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", exception.getStatusCode()
                .value());
        response.put("message", exception.getReason());
        response.put("path", request.getRequestURI());
        response.put("method", request.getMethod());
        return ResponseEntity.status(exception.getStatusCode())
                .body(response);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleRequestBodyValidationErrorExceptions(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<Map<String, String>> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of("field", error.getField(), "message",
                        Objects.requireNonNullElse(error.getDefaultMessage(), "Invalid value")))
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", 400);
        response.put("errors", errors);
        response.put("path", request.getRequestURI());
        response.put("method", request.getMethod());
        return ResponseEntity.badRequest()
                .body(response);

    }

}
