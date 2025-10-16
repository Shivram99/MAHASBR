package com.mahasbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;

@Service
public class ReginWiseDataServiceImpl implements RegionWiseDataService{
	
//	@Autowired
//    private RegionRepository regionRepository; 
	
	@Autowired
	MstRegistryDetailsPageRepository mstRegistryDetailsPageRepository;
	
	@Autowired
	DistrictMasterRepository districtMasterRepository;

//		@Override
//		    public List<RegionEntity> getAllRegions() {
//		        return regionRepository.findAll();
//	}
			@Override
			public List<DistrictMaster> getAllDistrict() {
				return districtMasterRepository.findAll();
			}
			@Override
			public Page<MstRegistryDetailsPageEntity> getAllByDistrictNames(List<String> matchingDistricts,
					Pageable pageable) {
				Page<MstRegistryDetailsPageEntity> mstRegistoryData=	mstRegistryDetailsPageRepository.findByDistricts(matchingDistricts, pageable);		
				return mstRegistoryData;
			}
			@Override
			public Page<MstRegistryDetailsPageEntity> getAllByDistrictNames(String districtName, Pageable pageable) {
				Page<MstRegistryDetailsPageEntity> mstRegistoryData=	mstRegistryDetailsPageRepository.findByDistrictName(districtName, pageable);		
				return mstRegistoryData;
			}

}
