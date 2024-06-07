package com.mahasbr.service;

import java.util.List;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.TalukaMaster;

public interface DistrictMasterService {

	public List<DistrictMaster> getAllDistrict();
	public List<TalukaMaster> getAllDistrictTaluka(Long districtCode);

//	public DistrictMaster insertDistrictDetail(DistrictMasterModel districtMasterModel);
//
//}
}