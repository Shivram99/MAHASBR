package com.mahasbr.controller;

import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.service.FileUploadService;

@RestController
@RequestMapping("/admin/api/test")
public class FileUploadController {

	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

	@Autowired
	FileUploadService fileUploadService;

	@PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		Workbook workbook = null;
		try {
			if (file.getOriginalFilename().endsWith(".csv")) {
				fileUploadService.processCSVFile(file);
			} else if (file.getOriginalFilename().endsWith(".xlsx")) {
				workbook = WorkbookFactory.create(file.getInputStream());
				fileUploadService.processExcelFile(workbook);
			} else {
				throw new IllegalArgumentException("Unsupported file type");
			}

			return ResponseEntity.ok("File uploaded and processed successfully");

		} catch (EncryptedDocumentException | IOException e) {
			logger.error(e.getMessage(), e);
			return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());

		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

}
