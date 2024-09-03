package com.mahasbr.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
/*
@ControllerAdvice
public class ExceptionHandlerController {

	  @ExceptionHandler({Exception.class, RuntimeException.class})
	    public ResponseEntity<HashMap> handleError(WebRequest req, Throwable ex) {
	        System.out.println(req.getDescription(false));
	        HashMap<String, String> mav = new HashMap<String, String>();
	        mav.put("msg", "Sorry, we couldn't find what you are looking for." + ex.getMessage());
	        mav.put("url", req.getDescription(false));
	        mav.put("error","");
	        return ResponseEntity.ok(mav);
	    }
	}*/
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
		// Your exception handling logic
		//return ResponseEntity.status(500).body("Failed to upload file: " + ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
	}
	@ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartException(MultipartException e) {
        return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
    }
	
}