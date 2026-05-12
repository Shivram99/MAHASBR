package com.mahasbr.service;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.TalukaMaster;

public interface TalukaMasterService {
	TalukaMaster create(TalukaMaster taluka);

	TalukaMaster update(Long id, TalukaMaster taluka);

	void delete(Long id);

	TalukaMaster getById(Long id);

	List<TalukaMaster> getAll();

	void importTalukasFromExcel(MultipartFile file) throws IOException;

	List<TalukaMaster> findByCensusDistrictCodeInAndIsActiveTrue(List<String> districtCode);

	List<TalukaMaster> findActiveByDistrictCode(String districtCode);

	Optional<TalukaMaster> findActiveByDistrictCodeAndTalukaCode(String districtCode, String talukaCode);
	
}
