package com.mahasbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.entity.VillageMaster;
import com.mahasbr.response.MessageResponse;
import com.mahasbr.service.CommonService;

@RestController
@RequestMapping("/user")
public class CommonController {
	@Autowired
	CommonService commonService;

	@GetMapping("/getDistrictData")
	public ResponseEntity<?> getDistrictDetails() {
		List<DistrictMaster> district = commonService.getAllDistrict();
		return ResponseEntity.ok(new MessageResponse(" District List", district));
	}

	@GetMapping("/getDistrictByCode/{CensusDistrictCode}")
	public ResponseEntity<?> getDistrictDetailByDistrictCode(@PathVariable long CensusDistrictCode) throws Exception {
		DistrictMaster district = commonService.getAllDistrictDistrictCode(CensusDistrictCode);
		return ResponseEntity.ok(new MessageResponse(" District List by District Code ", district));
	}

	@GetMapping("/getTalukaByCode/{CensusDistrictCode}")
	public ResponseEntity<?> getTalukaDetailByDistrictCode(@PathVariable long CensusDistrictCode) throws Exception {
		List<TalukaMaster> taluka = commonService.getAllTalukaByDistrictCode(CensusDistrictCode);
		return ResponseEntity.ok(new MessageResponse(" Taluka List by District Code ", taluka));
	}

	@GetMapping("/getVillagetByCode/{censusTalukaCode}")
	public ResponseEntity<?> getVillageDetailByTalukaCode(@PathVariable long censusTalukaCode) throws Exception {
		List<VillageMaster> village = commonService.getAllVillageTalukaCode(censusTalukaCode);
		return ResponseEntity.ok(new MessageResponse(" village List by taluka Code ", village));
	}

}
