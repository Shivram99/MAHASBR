package com.mahasbr.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;


public interface CircularService {

String processPDFFile(MultipartFile file) throws IOException;

String deleteFile();

boolean detectMaliciousJavaScript(String jsCode);

String extractTextFromPDF(InputStream inputStream);

boolean isValidPDF(InputStream inputStream);

}
