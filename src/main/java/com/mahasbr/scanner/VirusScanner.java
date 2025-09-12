package com.mahasbr.scanner;


import java.nio.file.Path;

public interface VirusScanner {
    boolean isClean(Path file) throws Exception;
}