package com.mahasbr.service;

import java.util.List;
import java.util.Optional;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.entity.User;
import com.mahasbr.entity.VillageMaster;
import com.mahasbr.model.TopicModel;

public interface CommonService {
	
	 public Optional<User> findByUsername(String username);

	public List<DistrictMaster> getAllDistrict();

	public DistrictMaster getAllDistrictDistrictCode(long censusDistrictCode)throws Exception ;

	public List<TalukaMaster> getAllTalukaByDistrictCode(long censusDistrictCode);

	public List<com.mahasbr.entity.VillageMaster> getAllVillageTalukaCode(long censusTalukaCode);


	public List<TopicModel> findMenuNameByRoleID(Long levelRoleVal);


	List<TopicModel> findSubMenuByRoleID(Long levelRoleVal);

	List<VillageMaster> getAllVillageTalukaCode(Long censusTalukaCode);


	
}
