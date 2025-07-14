package com.mahasbr.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.service.TalukaMasterService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/talukas")
public class TalukaMasterController {

	private final TalukaMasterService service;

	public TalukaMasterController(TalukaMasterService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<TalukaMaster> create(@Valid @RequestBody TalukaMaster taluka) {
		return new ResponseEntity<>(service.create(taluka), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<TalukaMaster> update(@PathVariable Long id, @Valid @RequestBody TalukaMaster taluka) {
		return ResponseEntity.ok(service.update(id, taluka));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<TalukaMaster> getById(@PathVariable Long id) {
		return ResponseEntity.ok(service.getById(id));
	}

	@GetMapping
	public ResponseEntity<List<TalukaMaster>> getAll() {
		return ResponseEntity.ok(service.getAll());
	}
	@PostMapping("/upload")
	public ResponseEntity<String> uploadTalukaExcel(@RequestParam("file") MultipartFile file) {
	    try {
	        service.importTalukasFromExcel(file);
	        return ResponseEntity.ok("Taluka data uploaded successfully.");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Failed to upload file: " + e.getMessage());
	    }
	}

}
