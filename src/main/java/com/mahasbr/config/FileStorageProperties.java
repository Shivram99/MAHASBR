package com.mahasbr.config;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Configuration
@ConfigurationProperties(prefix = "app.file")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileStorageProperties {

    private String uploadDir;
    private long maxFileSizeMb;
    private Set<String> allowedExtensions;
    private Set<String> allowedMimePrefixes;
    private String virusScannerCommand;

}
