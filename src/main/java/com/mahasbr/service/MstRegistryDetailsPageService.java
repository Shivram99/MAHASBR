package com.mahasbr.service;

import org.springframework.web.multipart.MultipartFile;

public interface MstRegistryDetailsPageService {

	void uploadRegiteryCSVFileForBRNGeneration(MultipartFile file);

}
