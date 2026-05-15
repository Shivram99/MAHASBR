package com.mahasbr.exception;

import org.springframework.http.HttpStatus;

public class ExportGenerationException extends RuntimeException {

	private final HttpStatus status;

	public ExportGenerationException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public ExportGenerationException(String message, HttpStatus status, Throwable cause) {
		super(message, cause);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
