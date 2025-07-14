package com.mahasbr.service;

import java.util.List;
import java.util.Optional;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.TalukaMaster;

public interface DistrictMasterService {

	public List<DistrictMaster> getAllDistrict();
	
	public Optional<DistrictMaster> findByDistrictCode(long long1);


	public List<DistrictMaster> readdataCsv();
	public List<TalukaMaster> getAllDistrictTaluka(List<Long> districtCodes);


//	public DistrictMaster insertDistrictDetail(DistrictMasterModel districtMasterModel);
//
//}
}