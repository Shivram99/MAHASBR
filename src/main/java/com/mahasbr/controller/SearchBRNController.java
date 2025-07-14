package com.mahasbr.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.dto.BrnSearchDTO;
import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.service.DistrictMasterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/BRN")
@RequiredArgsConstructor
public class SearchBRNController {

	 private final DistrictMasterService service;
	 
	 
	
	@GetMapping("/districts")
    public ResponseEntity<List<DistrictMaster>> getAll() {
        return ResponseEntity.ok(service.findByIsActiveTrue());
    }
	
	
	@PostMapping("/searchBRN")
    public ResponseEntity<?> searchBRN(@Valid @RequestBody BrnSearchDTO request) {
        
        return (ResponseEntity<?>) ResponseEntity.ok();
    }
	/*
	 * @GetMapping("/searchBRN") public ResponseEntity<List<DistrictMaster>>
	 * getSerachBRN() { return ResponseEntity.ok(service.findByIsActiveTrue()); }
	 */
}
