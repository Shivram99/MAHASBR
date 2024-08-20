package com.mahasbr.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.service.CircularService;
import com.mahasbr.service.CommonService;

@RestController
@RequestMapping("/admin/api/circular")
public class CircularController {

	private static final Logger logger = LoggerFactory.getLogger(CircularController.class);

	@Autowired
	private CircularService circularservice;
	@Autowired
	CommonService commonService;

	@PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		String fileType = file.getContentType();
		if (file.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file provided or file is empty.");
		}

		if (!"application/pdf".equals(fileType)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Unsupported file type. Only PDF files are allowed.");
		}

		if (file.getSize() >= 1048576) { // 1MB size limit
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size exceeds limit of 1MB.");
		}
		try {
			// Validate file content
			if (!circularservice.isValidPDF(file.getInputStream())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid PDF file.");
				// return "Invalid PDF file.";
			}
			// Extract JavaScript and check for malicious content
			String jsCode = circularservice.extractTextFromPDF(file.getInputStream());
			if (circularservice.detectMaliciousJavaScript(jsCode)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body("Malicious JavaScript detected! File will not be processed.");
			} else {
				// Pass the MultipartFile to the service method
				String filePath = circularservice.processPDFFile(file);
				return ResponseEntity.status(HttpStatus.OK)
						.body("PDF processed successfully.File saved at: " + filePath);
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Error processing file", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload file: Error processing PDF.");
		}
	}
}