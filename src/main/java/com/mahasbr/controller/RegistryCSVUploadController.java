package com.mahasbr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.service.MstRegistryDetailsPageService;

@RestController
@RequestMapping("/api/auth")
public class RegistryCSVUploadController {

	@Autowired
	MstRegistryDetailsPageService mstRegistryDetailsPageService;
	
	 @PostMapping("/upload")
	    public ResponseEntity<String> uploadExcelFile(@RequestParam ("File")MultipartFile file) {
	        if (file.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload.");
	        }
	        mstRegistryDetailsPageService.uploadRegiteryCSVFileForBRNGeneration(file);
	        
	       
			return null;
	 }
}
