package com.mahasbr.config;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

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
	}