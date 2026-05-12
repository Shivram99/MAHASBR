package com.mahasbr.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.service.DistrictMasterService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/detailsPage")
@RequiredArgsConstructor
public class BRNDetailsDashbordController {
	
	private static final Logger logger = LoggerFactory.getLogger(BRNDetailsDashbordController.class);
	private final DistrictMasterService districtservice;
	
	@GetMapping("/districts")
	public ResponseEntity<List<DistrictMaster>> getAll() {
		return ResponseEntity.ok(districtservice.findByIsActiveTrue());
	}
	
}
