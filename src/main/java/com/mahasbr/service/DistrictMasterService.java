package com.mahasbr.service;

import java.util.List;
import java.util.Optional;

import com.mahasbr.entity.DistrictMaster;

public interface DistrictMasterService {

	Optional<DistrictMaster> findByDistrictCode(long long1);

	public List<DistrictMaster> readdataCsv();


//	public DistrictMaster insertDistrictDetail(DistrictMasterModel districtMasterModel);
//
//}
}