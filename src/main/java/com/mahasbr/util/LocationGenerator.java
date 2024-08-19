package com.mahasbr.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.service.StatesMasterService;

@Component
public class LocationGenerator {
	
	@Autowired
	StatesMasterService statesMasterService;
	
	@Autowired
	DistrictMasterRepository districtMasterRepository;
	
	
}
