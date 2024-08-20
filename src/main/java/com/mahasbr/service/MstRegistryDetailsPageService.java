package com.mahasbr.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.MstRegistryDetailsPageEntity;

public interface MstRegistryDetailsPageService {

	void uploadRegiteryCSVFileForBRNGeneration(MultipartFile file);

	List<MstRegistryDetailsPageEntity> getsearchBRNAndEstablishmentDetails(String district, String brnNo,
			String establishment);

}
