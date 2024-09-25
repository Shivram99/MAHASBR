package com.mahasbr.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mahasbr.util.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	 @ExceptionHandler(ResourceNotFoundException.class)
	    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
	        ApiResponse<Void> response = new ApiResponse<>(ex.getMessage(), null);
	        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	    }

	    @ExceptionHandler(ResourceAlreadyExistsException.class)
	    public ResponseEntity<ApiResponse<Void>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
	        ApiResponse<Void> response = new ApiResponse<>(ex.getMessage(), null);
	        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	    }

	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex) {
	        ApiResponse<Void> response = new ApiResponse<>("An unexpected error occurred", null);
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
}
