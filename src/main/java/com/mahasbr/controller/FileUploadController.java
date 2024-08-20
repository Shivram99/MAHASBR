package com.mahasbr.controller;

import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
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
import com.mahasbr.service.FileUploadService;

@RestController
@RequestMapping("/admin")
public class FileUploadController {

//	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

	@Autowired
	FileUploadService fileUploadService;
	@Autowired
	CommonService commonService;

	@Autowired
	CircularService circularservice;

	@PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadFiles(@RequestParam("file") MultipartFile file)
			throws EncryptedDocumentException, InvalidFormatException {
		String content = null;
		Workbook workbook = null;

		try {
			String filename = file.getOriginalFilename();
			if (filename == null || filename.trim().isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file provided or file is empty.");
			}
			// Determine file type and validate
			if (file.getOriginalFilename().endsWith(".pdf")) {
				if (!circularservice.isValidPDF(file.getInputStream())) {
					return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid PDF file.");
				}
				content = circularservice.extractTextFromPDF(file.getInputStream());

			} else if (file.getOriginalFilename().endsWith(".xlsx")) {
				if (!commonService.isValidExcel(file)) {
					return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid Excel file.");
				}
				workbook = WorkbookFactory.create(file.getInputStream());
				content = commonService.extractTextFromXlsx(file.getInputStream());

			} else if (file.getOriginalFilename().endsWith(".csv")) {
				if (!commonService.isValidCSV(file)) {
					return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid CSV file.");
				}
				content = commonService.extractTextFromCsv(file.getInputStream());
			} else {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Unsupported file type.");
			}

			// Check for malicious content
			if (commonService.detectMaliciousContent(content)) {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
						.body("Malicious content detected! File will not be processed.");
			}

			// Process the file if no malicious content detected
			if (file.getOriginalFilename().endsWith(".pdf")) {
				circularservice.processPDFFile(file);
			} else if (file.getOriginalFilename().endsWith(".xlsx")) {
				fileUploadService.processExcelFile(workbook);
			} else if (file.getOriginalFilename().endsWith(".csv")) {
				fileUploadService.processCSVFile(file);
			}

			return ResponseEntity.status(HttpStatus.OK).body("File processed successfully.");

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing file.");
		} finally {
			// Close workbook if opened
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}