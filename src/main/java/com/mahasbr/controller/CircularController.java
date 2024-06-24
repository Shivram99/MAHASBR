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

import com.itextpdf.text.pdf.PRTokeniser;
import com.itextpdf.text.pdf.PdfReader;
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
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		String fileType = file.getContentType();

		try {
			if (file.getBytes().length > 0) {
				if (fileType == null || fileType.trim().length() == 0) {
					return ResponseEntity.status(400).body("Unknown file type specified!");
				}

				boolean isSafe = false;
				File tmpFile = null;
				Path tmpPath = null;

				try {
					tmpFile = File.createTempFile("uploaded-", null);
					tmpPath = tmpFile.toPath();

					try (OutputStream os = new FileOutputStream(tmpFile)) {
						os.write(file.getBytes());
						os.close();
						os.flush();
					}

					if ("application/pdf".equals(fileType)) {

						try {

							if (commonService.processPdf(tmpFile.getPath().toString())) {

							}

							isSafe = commonService.isSafe(tmpFile);
							if (!isSafe) {
								commonService.safelyRemoveFile(tmpPath);
								return ResponseEntity.status(500).body("Failed to upload file: Unsafe PDF file.");
							}
						} catch (Exception e) {
							logger.warn("Cannot safely remove file !", e);
						}

					}

					if (file.getSize() >= 1048576 || !fileType.equals("application/pdf")) {
						return ResponseEntity.status(500).body("Failed to upload file: Invalid file type or size.");
					} else {
						try {
							String filePath = circularservice.processPDFFile(file);
							return ResponseEntity.ok("File uploaded and saved successfully at: " + filePath);
						} catch (IOException e) {
							e.printStackTrace();
							return ResponseEntity.status(500).body("Failed to upload file: Error processing PDF.");
						}
					}
				} catch (Exception e) {
					if (tmpPath != null) {
						commonService.safelyRemoveFile(tmpPath);
					}
				}
			}
		} catch (IOException e) {
			logger.warn("Cannot safely remove file !", e);
			// e.printStackTrace();
		}

		return ResponseEntity.status(400).body("No file provided or file is empty.");
	}

	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteFile() {
		String filePath = circularservice.deleteFile();
		return ResponseEntity.ok(filePath);

	}
}