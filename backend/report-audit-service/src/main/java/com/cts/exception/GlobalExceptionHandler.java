package com.cts.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.cts.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handles Resource Not Found (Using your specific custom exception)
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(InvalidRequestException ex) {
        return new ResponseEntity<>(
            ApiResponse.error("Resource Not Found: " + ex.getMessage()), 
            HttpStatus.NOT_FOUND
        );
    }

    // 2. Handles Validation and Business Logic Errors
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(IllegalArgumentException ex) {
        return new ResponseEntity<>(
            ApiResponse.error("Validation Error: " + ex.getMessage()), 
            HttpStatus.BAD_REQUEST
        );
    }

    // 3. Handles Postman Date/Time Format Errors 
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String typeName = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("Parameter '%s' should be of type '%s'. Ensure dates follow ISO format (yyyy-MM-ddTHH:mm:ss).", 
                ex.getName(), typeName);
        return new ResponseEntity<>(
            ApiResponse.error(message), 
            HttpStatus.BAD_REQUEST
        );
    }

    // 4. Handles Spring Boot @Valid failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldError() != null ? 
                         ex.getBindingResult().getFieldError().getDefaultMessage() : "Validation failed";
        return new ResponseEntity<>(
            ApiResponse.error("Argument Validation Failed: " + message), 
            HttpStatus.BAD_REQUEST
        );
    }

    // 5. Catch-all for RuntimeExceptions (Database errors, NullPointer, etc.)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntime(RuntimeException e) {
        return new ResponseEntity<>(
            ApiResponse.error("Operation failed: " + e.getMessage()), 
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // 6. Final Catch-all for unexpected crashes
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalError(Exception ex) {
        return new ResponseEntity<>(
            ApiResponse.error("An unexpected system failure occurred: " + ex.getMessage()), 
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}