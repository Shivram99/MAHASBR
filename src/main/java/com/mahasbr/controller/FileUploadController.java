package com.mahasbr.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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

import com.mahasbr.service.CommonService;
import com.mahasbr.service.FileUploadService;

@RestController
@RequestMapping("/admin")
public class FileUploadController {

	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

	@Autowired
	FileUploadService fileUploadService;
	@Autowired
	CommonService commonService;

	@PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		Workbook workbook = null;
		try {
			String filename = file.getOriginalFilename();
			if (filename == null || filename.trim().isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file provided or file is empty.");
			}

			if (filename.endsWith(".csv")) {
				fileUploadService.processCSVFile(file);
				return ResponseEntity.ok("CSV file uploaded and processed successfully.");
			} else if (filename.endsWith(".xlsx")) {
				workbook = WorkbookFactory.create(file.getInputStream());
				String fileType = file.getContentType();

				if (fileType == null || fileType.trim().isEmpty()
						|| !fileType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type or size.");
				}

				boolean isSafe = false;
				File tmpFile = null;
				Path tmpPath = null;

				tmpFile = File.createTempFile("uploaded-", ".xlsx");
				tmpPath = tmpFile.toPath();

				try (FileOutputStream os = new FileOutputStream(tmpFile)) {
					os.write(file.getBytes());
				}

				isSafe = commonService.isSafe(tmpFile);
				if (!isSafe) {
					commonService.safelyRemoveFile(tmpPath);
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.body("Failed to upload file: Unsafe file.");
				}

				fileUploadService.processExcelFile(workbook);
				return ResponseEntity.ok("Excel file uploaded and processed successfully.");
			} else {
				throw new IllegalArgumentException("Unsupported file type");
			}

		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to upload file: " + e.getMessage());

		} finally {
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