package com.mahasbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.service.DistrictMasterService;

@RestController
@RequestMapping("/developer")
public class DistrictMasterController {
	@Autowired
	DistrictMasterService districtMasterService;

	@Autowired
	DistrictMasterRepository districtMasterRepository;

//	@PostMapping("/district")
//	public ResponseEntity<?> postDistrictDetails(@RequestBody DistrictMasterModel districtMasterModel) {
//		DistrictMaster district = districtMasterService.insertDistrictDetail(districtMasterModel);
//		return ResponseEntity.ok(new MessageResponse("Added successfully!", district));
//	}

	@GetMapping
	public @ResponseBody List<DistrictMaster> readCSV() {
		List<DistrictMaster> districts = districtMasterService.readdataCsv();
		return districts;	
	}
}