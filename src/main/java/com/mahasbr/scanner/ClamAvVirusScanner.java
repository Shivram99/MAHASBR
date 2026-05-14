package com.mahasbr.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.mahasbr.config.FileStorageProperties;

@Component
public class ClamAvVirusScanner 
implements VirusScanner {
    private static final Logger logger = LoggerFactory.getLogger(ClamAvVirusScanner.class);

    private final String command;
    private final boolean virusScanRequired;

    public ClamAvVirusScanner(FileStorageProperties properties) {
        this.command = properties.getVirusScannerCommand();
        this.virusScanRequired = properties.isVirusScanRequired();
    }

    @Override
    public boolean isClean(Path file) throws Exception {
        if (!StringUtils.hasText(command)) {
            if (virusScanRequired) {
                throw new IllegalStateException("Virus scanning is required but no scanner command is configured.");
            }
            return true;
        }

        List<String> cmd = List.of(command, "--fdpass", file.toAbsolutePath().toString());
        Process process;
        try {
            process = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        } catch (IOException ex) {
            if (virusScanRequired) {
                throw new IllegalStateException(
                        "Virus scanning is required but the scanner command '" + command + "' is unavailable.", ex);
            }
            logger.warn("Virus scanner command '{}' is unavailable. Skipping scan for {}", command, file);
            return true;
        }

        StringBuilder output = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exit = process.waitFor();
        if (exit == 0) return true;   // clean
        if (exit == 1) return false;  // infected
        throw new RuntimeException("Virus scan failed: " + output);
    }
}
