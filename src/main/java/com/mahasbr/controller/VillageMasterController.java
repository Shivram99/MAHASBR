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

import com.mahasbr.entity.VillageMaster;
import com.mahasbr.service.VillageMasterService;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/villages")
public class VillageMasterController {

    private final VillageMasterService service;

    public VillageMasterController(VillageMasterService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VillageMaster> create(@Valid @RequestBody VillageMaster village) {
        return new ResponseEntity<>(service.create(village), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VillageMaster> update(@PathVariable Long id, @Valid @RequestBody VillageMaster village) {
        return ResponseEntity.ok(service.update(id, village));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VillageMaster> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<VillageMaster>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            service.importFromExcel(file);
            return ResponseEntity.ok("Village data uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }

}

