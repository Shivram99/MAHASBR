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

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.service.DistrictMasterService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/districts")
public class DistrictMasterController {

    private final DistrictMasterService service;

    public DistrictMasterController(DistrictMasterService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DistrictMaster> create(@Valid @RequestBody DistrictMaster district) {
        return new ResponseEntity<>(service.create(district), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DistrictMaster> update(@PathVariable Long id, @Valid @RequestBody DistrictMaster district) {
        return ResponseEntity.ok(service.update(id, district));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DistrictMaster> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<DistrictMaster>> getAll() {
        return ResponseEntity.ok(service.findByIsActiveTrue());
    }
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadDistrictExcel(@RequestParam("file") MultipartFile file) {
        try {
            service.importDistrictsFromExcel(file);
            return ResponseEntity.ok("Districts uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
    }
}
