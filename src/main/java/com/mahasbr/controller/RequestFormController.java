package com.mahasbr.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.dto.RequestFormDTO;
import com.mahasbr.service.RequestFormService;
import com.mahasbr.util.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/citizenSearch")
public class RequestFormController {

	
	    @Autowired
	    private RequestFormService service;

	    @PostMapping("/requestForm")
	    public ResponseEntity<?> submit(@Valid @RequestBody RequestFormDTO request) {
	    	 String reqId = service.saveRequest(request);

	    	    return ResponseEntity.ok(Map.of(
	    	            "message", "Request submitted successfully",
	    	            "requestId", reqId
	    	    ));
	    }
	    
	    @GetMapping("/all")
	    public ResponseEntity<ApiResponse<List<RequestFormDTO>>> getAllRequests() {
	        List<RequestFormDTO> list = service.getAllRequests();
	        return ResponseEntity.ok(
	                new ApiResponse<>(true, "Fetched successfully", list)
	        );
	    }
}
