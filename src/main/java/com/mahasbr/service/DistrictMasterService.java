package com.mahasbr.service;

import java.util.List;
import java.util.Optional;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.TalukaMaster;

public interface DistrictMasterService {

	public List<DistrictMaster> getAllDistrict();
	public List<TalukaMaster> getAllDistrictTaluka(Long districtCode);
	public Optional<DistrictMaster> findByDistrictCode(long long1);

}