package com.mahasbr.service;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.config.FileStorageProperties;
import com.mahasbr.scanner.VirusScanner;
import com.mahasbr.validators.FileValidator;

@Service
public class FileStorageService {

    private final Path uploadDir;
    private final FileValidator validator;
    private final VirusScanner scanner;

    public FileStorageService(FileStorageProperties props, FileValidator validator, VirusScanner scanner) throws Exception {
        if (props.getUploadDir() == null || props.getUploadDir().isBlank()) {
            throw new IllegalStateException("Upload directory is not configured. Set app.file.upload-dir or FILE_UPLOAD_DIR.");
        }

        this.uploadDir = Paths.get(props.getUploadDir()).toAbsolutePath().normalize();
        this.validator = validator;
        this.scanner = scanner;

        try {
            Files.createDirectories(uploadDir);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Unable to create upload directory '" + uploadDir
                            + "'. Ensure the container user has write permission to this path.",
                    ex);
        }
    }

    public String store(MultipartFile file) throws Exception {
        // 1. Validate
        validator.validate(file);

        // 2. Generate secure filename
        String extension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf('.') + 1);
        String safeName = UUID.randomUUID() + "." + extension;
        Path target = uploadDir.resolve(safeName);

        // 3. Save to disk
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        // 4. Virus scan
        if (!scanner.isClean(target)) {
            Files.deleteIfExists(target);
            throw new SecurityException("File is infected");
        }

        return safeName;
    }
}
