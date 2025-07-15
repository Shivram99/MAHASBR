package com.mahasbr.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.entity.RegionEntity;

public interface RegionWiseDataService {

	List<RegionEntity> getAllRegions();

	List<DistrictMaster> getAllDistrict();

	Page<MstRegistryDetailsPageEntity> getAllByDistrictNames(List<String> matchingDistricts, Pageable pageable);

	Page<MstRegistryDetailsPageEntity> getAllByDistrictNames(String districtName, Pageable pageable);
}
