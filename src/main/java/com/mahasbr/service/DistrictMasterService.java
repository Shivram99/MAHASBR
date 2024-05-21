package com.mahasbr.service;

import org.springframework.stereotype.Service;

import com.mahasbr.entity.DistrictMaster;


@Service
public interface DistrictMasterService {

	DistrictMaster insertDistrictDetail(DistrictMaster districtMaster);
	
}
