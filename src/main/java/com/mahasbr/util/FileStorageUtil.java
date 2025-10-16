package com.mahasbr.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileStorageUtil — a reusable utility for handling file upload and retrieval.
 * 
 * ✅ Works across local and server environments.
 * ✅ Reads base directory from application properties.
 * ✅ Automatically creates missing directories.
 * ✅ Returns relative file paths safe for database storage.
 */
@Component
public class FileStorageUtil {

    @Value("${app.upload.base-dir:}") // configurable via application.properties
    private String configuredBaseDir;

    private static final String DEFAULT_DIR = "E:/SBR_Workspace/MAHASBR/upload/"; // fallback if not configured

    /**
     * Save a file into a specific sub-directory (like "circulars", "notices").
     * Returns the relative file path (e.g. "circulars/172827272_file.pdf")
     */
    public String saveFile(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file cannot be empty.");
        }

        // Determine the base upload directory (configurable or fallback)
        String baseDir = (configuredBaseDir == null || configuredBaseDir.isEmpty())
                ? System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + DEFAULT_DIR
                : configuredBaseDir;

        // Construct target upload directory path
        Path uploadPath = Paths.get(baseDir, subDirectory);

        try {
            // Create directories if missing
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate safe unique file name
            String originalFileName = Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\s+", "_");
            String fileName = System.currentTimeMillis() + "_" + originalFileName;

            // Resolve final destination path
            Path destination = uploadPath.resolve(fileName);

            // Copy file to destination (replace if exists)
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            // Return relative path stored in DB
//            return subDirectory + "/" + fileName;
            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }

    /**
     * Resolve absolute path for any stored file using its relative path.
     */
    public Path getAbsolutePath(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("Relative path cannot be null or blank");
        }

        String baseDir = (configuredBaseDir == null || configuredBaseDir.isEmpty())
                ? System.getProperty("user.dir") + FileSystems.getDefault().getSeparator() + DEFAULT_DIR
                : configuredBaseDir;

        return Paths.get(baseDir, relativePath);
    }

    /**
     * Checks if a file exists at the resolved absolute path.
     */
    public boolean fileExists(String relativePath) {
        return Files.exists(getAbsolutePath(relativePath));
    }
}

