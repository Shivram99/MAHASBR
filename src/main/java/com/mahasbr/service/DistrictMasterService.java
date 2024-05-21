package com.mahasbr.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.model.DistrictMasterModel;
import com.mahasbr.repository.DistrictMasterRepository;

public interface DistrictMasterService {

	public DistrictMaster insertDistrictDetail(DistrictMaster districtMaster);
}
