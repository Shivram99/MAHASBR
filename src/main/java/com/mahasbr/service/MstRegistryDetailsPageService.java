package com.mahasbr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.model.BRNGenerationRecordCount;

public interface MstRegistryDetailsPageService {

	BRNGenerationRecordCount uploadRegiteryCSVFileForBRNGeneration(MultipartFile file);

	List<MstRegistryDetailsPageEntity> getsearchBRNAndEstablishmentDetails(String district, String brnNo,
			String establishment);

	Page<MstRegistryDetailsPageEntity> getAllRegistoryDetails(Pageable pageable);

	Optional<MstRegistryDetailsPageEntity> getBRNDetails(String brn);
	
	Page<MstRegistryDetailsPageEntity> getBRNData(String brn ,Pageable pageable);

	Page<MstRegistryDetailsPageEntity> getPostLoginDashboardData(Pageable pageable, List<Long> selectedDistrictIds,
			List<Long> selectedTalukaIds, String registerDateFrom, String registerDateTo);

}
