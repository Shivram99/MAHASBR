package com.mahasbr.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
		;
		circularRepository.deleteAll();
		return "Data Delete sucessfully";
	}

}
