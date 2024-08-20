package com.mahasbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.DetailsPage;
import com.mahasbr.service.DashboardService;

@RestController
@RequestMapping("/user")
public class DashboardController {
	@Autowired
	DashboardService dashboardService;

//	@GetMapping("/records/TimeAndIndustryWise")
//	public ResponseEntity<List<DetailsPage>> getTimeAndIndustryWise() {
//		 List<DetailsPage> records =dashboardService.getTimeAndIndustryWise();
//		return ResponseEntity.ok(records);
//	}

	@GetMapping("/records/TimeAndIndustryWise")
	public ResponseEntity<?> getTimeAndIndustryWise() {
	    List<DetailsPage> records = dashboardService.getTimeAndIndustryWise();

	    if (records != null && !records.isEmpty()) {
	        return ResponseEntity.ok(records);
	    } else {
	        String message = "No records found for the specified time and industry criteria.";
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
	    }
	}

	@GetMapping("/records/Districtandindustrieswise")
	public ResponseEntity<?> getIndustrywiseStats() {
	    List<DetailsPage> records = dashboardService.getIndustryWiseStats();

	    if (records != null && !records.isEmpty()) {
	        return ResponseEntity.ok(records);
	    } else {
	        String message = "No records found for the specified district and industry criteria.";
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
	    }
	}

	@GetMapping("/records/ActandtimeWise")
	public ResponseEntity<?> getGraphData() {
	    List<DetailsPage> records = dashboardService.getActandtimeWiseStats();

	    if (records != null && !records.isEmpty()) {
	        return ResponseEntity.ok(records);
	    } else {
	        String message = "No records found for the specified act and time criteria.";
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
	    }
	}

	@GetMapping("/records/DistrictandActWise")
	public ResponseEntity<?> getLatestYearGraphData() {
	    List<DetailsPage> records = dashboardService.getDistrictAndActWiseStatsForLatestYear();

	    if (records != null && !records.isEmpty()) {
	        return ResponseEntity.ok(records);
	    } else {
	        String message = "No records found for the specified district and act criteria for the latest year.";
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
	    }
	}

	@GetMapping("/records/DistricrtAndTimeWise")
	public ResponseEntity<?> getDistricrtAndTimeWise() {
	    List<DetailsPage> records = dashboardService.getDistricrtAndTimeWise();

	    if (records != null && !records.isEmpty()) {
	        return ResponseEntity.ok(records);
	    } else {
	        String message = "No records found for the specified district and time criteria.";
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
	    }
	}
	
}
