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

import com.mahasbr.entity.Village;
import com.mahasbr.service.VillageService;

@RestController
@RequestMapping("/api/villages")
public class VillageController {

    @Autowired
    private VillageService villageService;

    @GetMapping
    public ResponseEntity<List<Village>> getAllVillages() {
        return ResponseEntity.ok(villageService.getAllVillages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Village> getVillageById(@PathVariable Long id) {
        return ResponseEntity.ok(villageService.getVillageById(id));
    }

    @PostMapping
    public ResponseEntity<Village> createVillage(@RequestBody Village village) {
        return ResponseEntity.ok(villageService.createVillage(village));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVillage(@PathVariable Long id) {
        villageService.deleteVillage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/taluka/{talukaId}")
    public ResponseEntity<List<Village>> getVillagesByTalukaId(@PathVariable Long talukaId) {
        return ResponseEntity.ok(villageService.getVillagesByTalukaId(talukaId));
    }
}
