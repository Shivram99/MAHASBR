package com.mahasbr.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class HttpStatusExceptionHandler {

	/*
	 * @ExceptionHandler(User.class)
	 * 
	 * @ResponseStatus(HttpStatus.NOT_FOUND) public ResponseEntity<String>
	 * handleUserNotFoundException(UserNotFoundException e) { return new
	 * ResponseEntity<>("User not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
	 * }
	 */
}