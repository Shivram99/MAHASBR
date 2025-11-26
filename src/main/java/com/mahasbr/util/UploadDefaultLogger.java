package com.mahasbr.util;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UploadDefaultLogger {

    private final MultipartProperties multipartProperties;

    public UploadDefaultLogger(MultipartProperties multipartProperties) {
        this.multipartProperties = multipartProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void printDefaults() {
        System.out.println("=== DEFAULT MULTIPART LIMITS ===");
        System.out.println("Max File Size     = " + multipartProperties.getMaxFileSize());
        System.out.println("Max Request Size  = " + multipartProperties.getMaxRequestSize());
        System.out.println("=================================");
    }
}
