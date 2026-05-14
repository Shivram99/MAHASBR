package com.mahasbr.validators;

import java.io.InputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.config.FileStorageProperties;
import com.mahasbr.service.FileUploadPolicy;

@Component
public class FileValidator {

    private final FileStorageProperties properties;
    private final Tika tika = new Tika();

    public FileValidator(FileStorageProperties properties) {
        this.properties = properties;
    }

    public void validate(MultipartFile file) {
        validate(file, null);
    }

    public void validate(MultipartFile file, FileUploadPolicy policy) {
        if (file == null) {
            throw new IllegalArgumentException("File is required");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        long maxFileSizeMb = resolveMaxFileSizeMb(policy);
        long maxBytes = maxFileSizeMb * 1024L * 1024L;
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException("File size exceeds limit of " + maxFileSizeMb + "MB");
        }

        String name = file.getOriginalFilename();
        if (name == null || !name.contains(".")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        Set<String> allowedExtensions = resolveAllowedExtensions(policy);
        if (!allowedExtensions.isEmpty() && !allowedExtensions.contains(ext)) {
            throw new IllegalArgumentException("Extension not allowed: " + ext);
        }

        try (InputStream in = file.getInputStream()) {
            String mime = tika.detect(in, name);
            if (!isMimeAllowed(mime, resolveAllowedMimePatterns(policy))) {
                throw new IllegalArgumentException("MIME type not allowed: " + mime);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to inspect uploaded file", ex);
        }
    }

    private long resolveMaxFileSizeMb(FileUploadPolicy policy) {
        if (policy != null && policy.maxFileSizeMb() > 0) {
            return policy.maxFileSizeMb();
        }
        return properties.getMaxFileSizeMb() > 0 ? properties.getMaxFileSizeMb() : 100;
    }

    private Set<String> resolveAllowedExtensions(FileUploadPolicy policy) {
        if (policy != null && policy.allowedExtensions() != null) {
            return policy.allowedExtensions();
        }
        return properties.getAllowedExtensions() != null ? properties.getAllowedExtensions() : Collections.emptySet();
    }

    private Set<String> resolveAllowedMimePatterns(FileUploadPolicy policy) {
        if (policy != null && policy.allowedMimeTypes() != null) {
            return policy.allowedMimeTypes();
        }
        return properties.getAllowedMimePrefixes() != null ? properties.getAllowedMimePrefixes() : Collections.emptySet();
    }

    private boolean isMimeAllowed(String mime, Set<String> allowedPatterns) {
        if (allowedPatterns == null || allowedPatterns.isEmpty()) {
            return true;
        }

        return allowedPatterns.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .anyMatch(pattern -> pattern.endsWith("/") ? mime.startsWith(pattern) : mime.equalsIgnoreCase(pattern));
    }
}
