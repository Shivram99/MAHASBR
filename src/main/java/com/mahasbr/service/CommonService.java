package com.mahasbr.service;

import java.util.List;
import java.util.Optional;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.entity.User;

public interface CommonService {
	
	 public Optional<User> findByUsername(String username);

	public List<DistrictMaster> getAllDistrict();

	public DistrictMaster getAllDistrictDistrictCode(long censusDistrictCode)throws Exception ;

	public List<TalukaMaster> getAllTalukaByDistrictCode(long censusDistrictCode);

	public List<com.mahasbr.entity.VillageMaster> getAllVillageTalukaCode(long censusTalukaCode);


	
}
