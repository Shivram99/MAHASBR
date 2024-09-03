package com.mahasbr.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.Circular;
import com.mahasbr.repository.CircularRepository;

@Service
@PropertySource("application.properties")
public class CircularServiceImpl implements CircularService {
	private static final Logger logger = LoggerFactory.getLogger(CircularServiceImpl.class);

	@Autowired
	private Environment env;

	@Autowired
	CircularRepository circularRepository;
	@Autowired
	CommonService commonService;

	@Override
	public String processPDFFile(MultipartFile file) throws IOException {

		// Ensure the directory exists or create it if it doesn't
		String fileUploadPath = null;
		logger.info((System.getProperty("os.name")));

		if (System.getProperty("os.name").contains("Windows")) {

			fileUploadPath = env.getProperty("mahasbrcircularfileupload.locationWindows");
		} else {
			fileUploadPath = env.getProperty("mahasbrcircularfileupload.locationLinux");
		}

		Path uploadDir = Paths.get(fileUploadPath);
		if (!Files.exists(uploadDir)) {
			Files.createDirectories(uploadDir);
		}

		// Generate a unique file name
		List<Circular> lastPdfFiles = circularRepository.findAll();
		long id = lastPdfFiles.stream().mapToLong(Circular::getId).max().orElse(0) + 1;
		String baseFileName = "circular_" + id + ".pdf";

		// Ensure the file name is unique by checking for existing files
		String fileName = baseFileName;
		Path filePath = uploadDir.resolve(fileName);
		int counter = 1;
		while (Files.exists(filePath)) {
			fileName = "circular_" + counter + ".pdf";
			filePath = uploadDir.resolve(fileName);
			counter++;
		}

		// Save the file to the specified location
		Files.copy(file.getInputStream(), filePath);

		// Save the file in the database
		Circular pdfFile = new Circular(fileName, filePath.toString());
		circularRepository.save(pdfFile);

		logger.info("PDF file saved successfully at: {}", filePath);
		return filePath.toString();
	}

	@Override
	public String deleteFile() {
		circularRepository.deleteAll();
		return "Data Delete sucessfully";
	}

	@Override
	public boolean detectMaliciousJavaScript(String jsCode) {
		if (jsCode.isEmpty()) {
			return false;
		}
		String[] suspiciousPatterns = { "eval", // Use of eval function
				"document.write", // Writing to document
				"unescape", // Unescaping strings (often used in obfuscation)
				"<script>", // Inline scripts
				"</script>", // Closing script tags
				"window.location", // Redirects
				"XMLHttpRequest", // AJAX requests
				"alert(", // Alerts
				"confirm(", // Confirm dialogs
				"prompt(", // Prompt dialogs
				"setTimeout(", // Timers
				"setInterval(", // Intervals
				"document.location", // Document location
				"location.href", // Location href
				"document.cookie", // Accessing cookies
				"document.domain" // Document domain
		};

		// Regular expressions for HTML tags
		String[] htmlTags = { "<[^>]*>", // Any HTML tag
				"</[^>]*>", // Closing HTML tag
				"<script[^>]*>", // Opening script tag
				"</script>", // Closing script tag
				"<iframe[^>]*>", // Opening iframe tag
				"</iframe>" // Closing iframe tag
		};
		for (String pattern : suspiciousPatterns) {
			if (jsCode.contains(pattern)) {
				return true;
			}
		}
		// Check for HTML tags
		for (String tag : htmlTags) {
			if (jsCode.matches("(?s).*" + tag + ".*")) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String extractTextFromPDF(InputStream inputStream) {
		StringBuilder textContent = new StringBuilder();
		try (PDDocument document = PDDocument.load(inputStream)) {
			// Extract text from PDF
			PDFTextStripper pdfStripper = new PDFTextStripper();
			textContent.append(pdfStripper.getText(document));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return textContent.toString();
	}

	@Override
	public boolean isValidPDF(InputStream inputStream) {
		try {
			// Check for PDF header
			byte[] header = new byte[5];
			inputStream.read(header, 0, 5);
			return new String(header).equals("%PDF-");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
