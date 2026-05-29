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

	Page<MstRegistryDetailsPageEntity> searchBrnRecords(Pageable pageable, String district, String taluka, String brn,
			String establishmentName);

	Page<MstRegistryDetailsPageEntity> getAllRegistoryDetails(Pageable pageable, String registerDateFrom, String registerDateTo);

	Optional<MstRegistryDetailsPageEntity> getBRNDetails(String brn);
	
	Page<MstRegistryDetailsPageEntity> getBRNData(String brn ,Pageable pageable, String registerDateFrom, String registerDateTo);

	Page<MstRegistryDetailsPageEntity> getPostLoginDashboardData(Pageable pageable, List<Long> selectedDistrictIds,
			List<Long> selectedTalukaIds, String registerDateFrom, String registerDateTo);


//	List<RegionEntity> getAllRegions();
//
//	List<DistrictMaster> getAllDistrict();
//
//	List<MstRegistryDetailsPageEntity> getAllByDistrictNames(List<String> matchingDistricts, Pageable pageable);

}
