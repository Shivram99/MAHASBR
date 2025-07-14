package com.mahasbr.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.StatesMaster;
import com.mahasbr.service.StatesMasterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/states")
@RequiredArgsConstructor
public class StatesMasterController {

	private final StatesMasterService service;

	@GetMapping
	public ResponseEntity<List<StatesMaster>> getAllStates() {
		List<StatesMaster> states = service.getAll();
		return ResponseEntity.ok(states);
	}

	@GetMapping("/{id}")
	public ResponseEntity<StatesMaster> getStateById(@PathVariable Long id) {
		StatesMaster state = service.getById(id);
		return ResponseEntity.ok(state);
	}

	@PostMapping
	public ResponseEntity<StatesMaster> createState(@Valid @RequestBody StatesMaster state) {
		StatesMaster createdState = service.create(state);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdState);
	}

	@PutMapping("/{id}")
	public ResponseEntity<StatesMaster> updateState(@PathVariable Long id,
													@Valid @RequestBody StatesMaster state) {
		StatesMaster updated = service.update(id, state);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteState(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadExcelFile(@RequestPart("file") MultipartFile file) {
	    // 1. Validate empty file
	    if (file.isEmpty()) {
	        return ResponseEntity.badRequest().body("File is empty. Please upload a valid Excel file.");
	    }

	    // 2. Validate file extension
	    if (!file.getOriginalFilename().toLowerCase().endsWith(".xlsx")) {
	        return ResponseEntity.badRequest().body("Invalid file type. Only .xlsx files are supported.");
	    }

	    // 3. Process and save data
	    try {
	        service.importStatesFromExcel(file);
	        return ResponseEntity.ok("File uploaded and data saved successfully.");
	    } catch (Exception ex) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error processing file: " + ex.getMessage());
	    }
	}


}
