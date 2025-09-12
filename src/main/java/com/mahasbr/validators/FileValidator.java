package com.mahasbr.validators;

import java.io.InputStream;
import java.util.Locale;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.config.FileStorageProperties;

@Component
public class FileValidator {

    private final FileStorageProperties properties;
    private final Tika tika = new Tika();

    public FileValidator(FileStorageProperties properties) {
        this.properties = properties;
    }

    public void validate(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        long maxBytes = properties.getMaxFileSizeMb() * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("File size exceeds limit of " + properties.getMaxFileSizeMb() + "MB");
        }

        String name = file.getOriginalFilename();
        if (name == null || !name.contains(".")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (!properties.getAllowedExtensions().contains(ext)) {
            throw new IllegalArgumentException("Extension not allowed: " + ext);
        }

        try (InputStream in = file.getInputStream()) {
            String mime = tika.detect(in);
            boolean allowed = properties.getAllowedMimePrefixes().stream().anyMatch(mime::startsWith);
            if (!allowed) {
                throw new IllegalArgumentException("MIME type not allowed: " + mime);
            }
        }
    }
}