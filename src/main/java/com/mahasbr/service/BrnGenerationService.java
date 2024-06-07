package com.mahasbr.service;

import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.DetailsPage;

public interface BrnGenerationService {

	String getVillageDtlByVillageName(DetailsPage details);



	void uploadExcelFile(MultipartFile file);

	

}
