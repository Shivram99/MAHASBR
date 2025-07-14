package com.mahasbr.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.entity.User;
import com.mahasbr.entity.VillageMaster;
import com.mahasbr.model.TopicModel;

public interface CommonService {
	
	 public Optional<User> findByUsername(String username);

	public List<DistrictMaster> getAllDistrict();

	public DistrictMaster getAllDistrictDistrictCode(String censusDistrictCode)throws Exception ;

	public List<TalukaMaster> getAllTalukaByDistrictCode(String censusDistrictCode);

	public List<VillageMaster> getAllVillageTalukaCode(String censusTalukaCode);


	public List<TopicModel> findMenuNameByRoleID(Long levelRoleVal);


	List<TopicModel> findSubMenuByRoleID(Long levelRoleVal);


		
	//public void processPdfFile(MultipartFile file) throws IOException;

	public boolean isValidExcel(MultipartFile file);

	public String extractTextFromXlsx(InputStream inputStream) throws IOException;

	public boolean isValidCSV(MultipartFile file);

	public String extractTextFromCsv(InputStream inputStream) throws IOException;

	public boolean detectMaliciousContent(String content);

	
}
