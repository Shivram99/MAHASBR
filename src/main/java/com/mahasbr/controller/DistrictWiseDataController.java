package com.mahasbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.service.DistrictWiseDataService;
import com.mahasbr.service.MstRegistryDetailsPageService;
import com.mahasbr.service.RegionWiseDataService;

@RestController
@RequestMapping("/api/auth")
public class DistrictWiseDataController {

	@Autowired
	MstRegistryDetailsPageService mstRegistryDetailsPageService;

	@Autowired
	DistrictWiseDataService districtWiseDataService;
	
	@Autowired
	RegionWiseDataService regionWiseDataService;
	
	@GetMapping("/registoryDistrictData/")
	public ResponseEntity<Page<MstRegistryDetailsPageEntity>> getMasterRegistoryDetails(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "siNo") String sortBy) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		List<DistrictMaster> districts = regionWiseDataService.getAllDistrict();
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		System.err.println("!123123 "+username);
		// Step 1: Match district name with the user's region
		for (DistrictMaster district : districts) {
	        String districtName = district.getDistrictName().toLowerCase();
	        System.err.println("dipali  "+districtName);
	        if (username.toLowerCase().contains(districtName)) {
	            // Fetch registry details if a district match is found
	            Page<MstRegistryDetailsPageEntity> registryDetails = regionWiseDataService
	                    .getAllByDistrictNames(districtName.toUpperCase(), pageable);
	            return ResponseEntity.ok(registryDetails);
	            
	        }
	    }

	    // Step 2: If no match was found, return a BAD_REQUEST response
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	}
}

