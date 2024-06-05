package com.mahasbr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.repository.DistrictMasterRepository;

@Service
public class DistrictMasterServiceImpl implements DistrictMasterService {
	@Autowired
	DistrictMasterRepository districtMasterRepository;



//	@Override
//	public DistrictMaster insertDistrictDetail(DistrictMasterModel districtMasterModel) {
//		DistrictMaster data = new DistrictMaster(districtMasterModel);
//		districtMasterRepository.save(data);
//		return data;
//	}
	
        



	@Override
	public Optional<DistrictMaster> findByDistrictCode(long long1) {
		return districtMasterRepository.findById(long1);
	}


}
