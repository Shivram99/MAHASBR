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
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.dto.RegistryMasterResponse;
import com.mahasbr.service.RegistryMasterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/registries")
@RequiredArgsConstructor
public class RegistryMasterController {

    private final RegistryMasterService registryMasterService;

    @GetMapping
    public ResponseEntity<List<RegistryMasterResponse>> getAllRegistries() {
        return ResponseEntity.ok(registryMasterService.getAllRegistries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistryMasterResponse> getRegistryById(@PathVariable Long id) {
        return ResponseEntity.ok(registryMasterService.getRegistryById(id));
    }

    @PostMapping
    public ResponseEntity<RegistryMasterResponse> createRegistry(
            @Valid @RequestBody RegistryMasterResponse request) {
        RegistryMasterResponse response = registryMasterService.createRegistry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistryMasterResponse> updateRegistry(
            @PathVariable Long id,
            @Valid @RequestBody RegistryMasterResponse request) {
        return ResponseEntity.ok(registryMasterService.updateRegistry(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistry(@PathVariable Long id) {
        registryMasterService.deleteRegistry(id);
        return ResponseEntity.noContent().build();
    }
}
