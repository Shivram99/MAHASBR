package com.mahasbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.TalukaMasterRepository;

@Service
public class DistrictMasterServiceImpl implements DistrictMasterService {
	
	@Autowired
	DistrictMasterRepository districtMasterRepository;
	@Autowired
	TalukaMasterRepository tōalukaMasterRepository;

	
	public List<DistrictMaster> getAllDistrict() {
	
		return districtMasterRepository.findAll();
	}
	
	public List<TalukaMaster> getAllDistrictTaluka(Long districtCode) {
		
		return tōalukaMasterRepository.findByCensusDistrictCode(districtCode);
	}

}
