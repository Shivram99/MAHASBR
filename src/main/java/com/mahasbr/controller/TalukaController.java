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

import com.mahasbr.entity.Taluka;
import com.mahasbr.service.TalukaService;

@RestController
@RequestMapping("/api/talukas")
public class TalukaController {

    @Autowired
    private TalukaService talukaService;

    @GetMapping
    public ResponseEntity<List<Taluka>> getAllTalukas() {
        return ResponseEntity.ok(talukaService.getAllTalukas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Taluka> getTalukaById(@PathVariable Long id) {
        return ResponseEntity.ok(talukaService.getTalukaById(id));
    }

    @PostMapping
    public ResponseEntity<Taluka> createTaluka(@RequestBody Taluka taluka) {
        return ResponseEntity.ok(talukaService.createTaluka(taluka));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaluka(@PathVariable Long id) {
        talukaService.deleteTaluka(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/district/{districtId}")
    public ResponseEntity<List<Taluka>> getTalukasByDistrictId(@PathVariable Long districtId) {
        return ResponseEntity.ok(talukaService.getTalukasByDistrictId(districtId));
    }
}