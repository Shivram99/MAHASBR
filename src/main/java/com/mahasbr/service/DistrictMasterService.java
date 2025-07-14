package com.mahasbr.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.DistrictMaster;

public interface DistrictMasterService {
	DistrictMaster create(DistrictMaster district);

	DistrictMaster update(Long id, DistrictMaster district);

	void delete(Long id);

	DistrictMaster getById(Long id);

	List<DistrictMaster> getAll();

	void importDistrictsFromExcel(MultipartFile file) throws IOException;
	
	List<DistrictMaster> findByIsActiveTrue();

}
