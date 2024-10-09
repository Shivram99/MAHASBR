package com.mahasbr.controller;

import java.util.ArrayList;
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
import com.mahasbr.entity.RegionEntity;
import com.mahasbr.service.MstRegistryDetailsPageService;
import com.mahasbr.service.RegionWiseDataService;

@RestController
@RequestMapping("/api/auth")
public class RegionWiseDataController {

	@Autowired
	MstRegistryDetailsPageService mstRegistryDetailsPageService;

	@Autowired
	RegionWiseDataService regionWiseDataService;

	@GetMapping("/registoryRegionData/")
	public ResponseEntity<Page<MstRegistryDetailsPageEntity>> getMasterRegistoryDetails(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "siNo") String sortBy) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		List<RegionEntity> regions = regionWiseDataService.getAllRegions();
		List<DistrictMaster> districts = regionWiseDataService.getAllDistrict();

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		Long matchedRegionId = null;

		// Step 1: Match region name with the user's region
		for (RegionEntity region : regions) {
			String regionName = region.getRegionName().toLowerCase();

			if (username.toLowerCase().contains(regionName)) {
				matchedRegionId = region.getId(); 
				break; 
			}
		}

		// Step 2: If a region match is found, check districts for the matched region ID
		if (matchedRegionId != null) {
			List<String> matchingDistricts = new ArrayList<>();

			// Step 3: Match the districts with the region ID
			for (DistrictMaster district : districts) {
				if (district.getRegionid().equals(matchedRegionId)) {
					matchingDistricts.add(district.getDistrictName()); 
				}
			}

			// Fetch and return registry details based on pagination
			Page<MstRegistryDetailsPageEntity> registryDetails = regionWiseDataService
					.getAllByDistrictNames(matchingDistricts, pageable);
			return ResponseEntity.ok(registryDetails);

		} else {
			// Handle the case where no region match is found
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

	}
}
