package com.mahasbr.service;

import java.util.Set;

public record FileUploadPolicy(
        long maxFileSizeMb,
        Set<String> allowedExtensions,
        Set<String> allowedMimeTypes) {
}
