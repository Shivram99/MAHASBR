package com.mahasbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.District;
import com.mahasbr.repository.DistrictRepository;

@Service
public class DistrictService {
	
	 @Autowired
	    private DistrictRepository districtRepository;

	    public List<District> getAllDistricts() {
	        return districtRepository.findAll();
	    }

	    public District getDistrictById(Long id) {
	        return districtRepository.findById(id).orElseThrow(() -> new RuntimeException("District not found"));
	    }

	    public District createDistrict(District district) {
	        return districtRepository.save(district);
	    }

	    public void deleteDistrict(Long id) {
	        districtRepository.deleteById(id);
	    }

	    public List<District> getDistrictsByStateId(Long stateId) {
	        return districtRepository.findByStateId(stateId);
	    }
   
}
