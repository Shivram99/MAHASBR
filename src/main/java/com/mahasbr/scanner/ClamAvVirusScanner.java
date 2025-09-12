package com.mahasbr.scanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Component;

import com.mahasbr.config.FileStorageProperties;

@Component
public class ClamAvVirusScanner 
implements VirusScanner {

    private final String command;

    public ClamAvVirusScanner(FileStorageProperties properties) {
        this.command = properties.getVirusScannerCommand();
    }

    @Override
    public boolean isClean(Path file) throws Exception {
        List<String> cmd = List.of(command, "--fdpass", file.toAbsolutePath().toString());
        Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();

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