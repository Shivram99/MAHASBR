package com.mahasbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.model.ActAndYearWiseModel;
import com.mahasbr.model.DistrictAndActWiseModel;
import com.mahasbr.model.DistrictAndIndustryModel;
import com.mahasbr.model.DistrictAndTimeWiseModel;
import com.mahasbr.model.IndustryAndTimeWiseModel;
import com.mahasbr.service.DashboardService;

@RestController
@RequestMapping("/user")
public class DashboradController {
	@Autowired
	DashboardService dashboardService;

	@GetMapping("/records/DistrictAndTimeWise")
	public ResponseEntity<?> getDistrictAndTimeWise(@RequestParam String district,
			@RequestParam String quarter, @RequestParam String operation) {

		// Call the service method with the provided parameters
		List<DistrictAndTimeWiseModel> records = dashboardService.getDistrictAndTimeWise(district, quarter,operation);

		// Check if records are found
		if (records != null && !records.isEmpty()) {
			return ResponseEntity.ok(records);
		} else {
			String message = "No records found for the specified district and time criteria";
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}
	}

	@GetMapping("/records/ActAndYearWise")
	public ResponseEntity<?> getActAndYearWise(@RequestParam String act, @RequestParam String year,  @RequestParam String operation) {
		List<ActAndYearWiseModel> records = dashboardService.getActAndYearWise(act, year,operation);
		if (records != null && !records.isEmpty()) {
			return ResponseEntity.ok(records);
		} else {
			String message = "No records found for the specified act and year";
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}
	}

	@GetMapping("/records/DistrictAndActWise")
	public ResponseEntity<?> getDistrictAndActWise(@RequestParam String district, @RequestParam String act,  @RequestParam String operation) {
		List<DistrictAndActWiseModel> records = dashboardService.getDistrictAndActWise(district, act, operation);
		if (records != null && !records.isEmpty()) {
			return ResponseEntity.ok(records);
		} else {
			String message = "No records found for the specified district and act";
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
		}
	}
	
	@GetMapping("/records/DistrictAndIndustryWise")
	public ResponseEntity<?> getDistrictAndEstateOwnerWise(@RequestParam String district, @RequestParam String estateOwner,  @RequestParam String operation) {
        List<DistrictAndIndustryModel> records = dashboardService.getDistrictAndEstateOwnerWise(district, estateOwner, operation);
        if (records != null && !records.isEmpty()) {
            return ResponseEntity.ok(records);
        } else {
            String message = "No records found for the specified district and estate owner";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
    }
	
	@GetMapping("/records/estateOwnerAndQuarterWise")
	public ResponseEntity<?> getEstateOwnerAndQuarterWise(@RequestParam String estateOwner, @RequestParam String quarter,@RequestParam String operation) {
	    List<IndustryAndTimeWiseModel> records = dashboardService.getEstateOwnerAndQuarterWise(estateOwner, quarter , operation);
	    if (records != null && !records.isEmpty()) {
	        return ResponseEntity.ok(records);
	    } else {
	        String message = "No records found for the specified estate owner and quarter.";
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
	    }
	}
}
