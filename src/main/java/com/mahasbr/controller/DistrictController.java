package com.mahasbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.response.MessageResponse;
import com.mahasbr.service.DistrictMasterService;

@RestController
@RequestMapping("/user")
public class DistrictController {
	
	@Autowired
	DistrictMasterService districtMasterService;
	
	@GetMapping("/getAllDistricts")
    public ResponseEntity<List<DistrictMaster>> getAllDistricts() {
		
        return ResponseEntity.ok(districtMasterService.getAllDistrict());
    }
//	@GetMapping("/getDistrictTaluka/{districtCode}")
//    public ResponseEntity<List<TalukaMaster>> getAllDistrictTaluka(@PathVariable Long districtCode) {
//		
//        return ResponseEntity.ok(districtMasterService.getAllDistrictTaluka( districtCode));
//    }
	
	@PostMapping(value="/getDistrictTaluka",consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> getTalukaDetailByDistrictCodes(@RequestBody List<Long> districtCodes) throws Exception {
	   List<TalukaMaster> talukas = districtMasterService.getAllDistrictTaluka(districtCodes);
	   System.out.println("talukas "+talukas.toString());
	    return ResponseEntity.ok(talukas);
	}

}
