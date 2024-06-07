package com.mahasbr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.entity.User;
import com.mahasbr.entity.VillageMaster;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.TalukaMasterRepository;
import com.mahasbr.repository.UserRepository;
import com.mahasbr.repository.VillageMasterRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CommonServiceImpl implements CommonService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	DistrictMasterRepository districtMasterRepository;
	
	@Autowired
	TalukaMasterRepository talukaMasterRepository;
	
	@Autowired
	VillageMasterRepository villageMasterRepository;

	@Override
	public Optional<User> findByUsername(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		return user;
	}

// for Get All District List 
	@Override
	public List<DistrictMaster> getAllDistrict(){
		return districtMasterRepository.findAll();
	}

	// for Get All District List by Districtcode
	@Override
	public DistrictMaster getAllDistrictDistrictCode(long censusDistrictCode) throws Exception {
		 Optional<DistrictMaster> districtCode = districtMasterRepository.findById(censusDistrictCode);
	        if (districtCode.isPresent())
	            return districtCode.get();
	        else {

	            throw new Exception("*** Id is not present ***");
	       }
	}

	@Override
	public List<TalukaMaster> getAllTalukaByDistrictCode(long censusDistrictCode) {
		 List<TalukaMaster> taluka= talukaMasterRepository.findBycensusDistrictCode(censusDistrictCode);		 
		return taluka;
	}

	@Override
	public List<VillageMaster> getAllVillageTalukaCode(long censusTalukaCode) {
		 List<VillageMaster> village= villageMasterRepository.findBycensusTalukaCode(censusTalukaCode);	
		return village;
	}
	


}
