package com.mahasbr.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

//    private static final String PDF_UPLOAD_DIR = "pdf_files/";

	@Autowired
	private CircularService circularservice;
	@Autowired
	CommonService commonService;

	@PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file)
			throws IllegalStateException, IOException {
		String fileType = file.getContentType();
		if (file.getBytes().length > 0) {
			boolean isSafe;
			File tmpFile = null;
			Path tmpPath = null;
			try {
				if ((fileType == null) || (fileType.trim().length() == 0)) {
					throw new IllegalArgumentException("Unknown file type specified !");
				}
				tmpFile = File.createTempFile("uploaded-", null);
				tmpPath = tmpFile.toPath();

				OutputStream os = new FileOutputStream(tmpFile);
				os.write(file.getBytes());
				os.close();
			} catch (Exception e) {
				commonService.safelyRemoveFile(tmpPath);
			}

			switch (file.getContentType()) {
			case "application/pdf":
				isSafe = commonService.isSafe(tmpFile);
				if (!isSafe) {
					commonService.safelyRemoveFile(tmpPath);
					ResponseEntity.status(500).body("Failed to upload file:");
				}
				break;
			}

			File f1 = null;
			if (file.getSize() >= 1048576 || !fileType.equals("application/pdf")) { // file size
				ResponseEntity.status(500).body("Failed to upload file: ");
			} else {
				String filePath;
				try {
					filePath = circularservice.processPDFFile(file);
					return ResponseEntity.ok("File uploaded and saved successfully at: " + filePath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return null;
	}

	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteFile() {
		String filePath = circularservice.deleteFile();
		return ResponseEntity.ok(filePath);

	}
}