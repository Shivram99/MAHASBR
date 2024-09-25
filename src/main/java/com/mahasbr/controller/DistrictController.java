package com.mahasbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.District;
import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.service.DistrictMasterService;
import com.mahasbr.service.DistrictService;

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
	
	  @Autowired
	    private DistrictService districtService;

//	    @GetMapping
//	    public ResponseEntity<List<District>> getAllDistricts() {
//	        return ResponseEntity.ok(districtService.getAllDistricts());
//	    }

	    @GetMapping("/{id}")
	    public ResponseEntity<District> getDistrictById(@PathVariable Long id) {
	        return ResponseEntity.ok(districtService.getDistrictById(id));
	    }

	    @PostMapping
	    public ResponseEntity<District> createDistrict(@RequestBody District district) {
	        return ResponseEntity.ok(districtService.createDistrict(district));
	    }

	    @DeleteMapping("/{id}")
	    public ResponseEntity<Void> deleteDistrict(@PathVariable Long id) {
	        districtService.deleteDistrict(id);
	        return ResponseEntity.noContent().build();
	    }

	    @GetMapping("/state/{stateId}")
	    public ResponseEntity<List<District>> getDistrictsByStateId(@PathVariable Long stateId) {
	        return ResponseEntity.ok(districtService.getDistrictsByStateId(stateId));
	    }

}
