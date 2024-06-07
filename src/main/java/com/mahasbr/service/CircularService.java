package com.mahasbr.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;


public interface CircularService {

	String processPDFFile(MultipartFile file) throws IOException;

	String deleteFile();

}
