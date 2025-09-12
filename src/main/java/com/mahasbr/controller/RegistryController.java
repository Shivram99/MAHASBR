package com.mahasbr.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.service.RegistryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/registries")
@RequiredArgsConstructor
public class RegistryController {

    private final RegistryService registryService;
//
//    @PostMapping
//    public ResponseEntity<RegistryMasterEntity> createRegistry(@Valid @RequestBody RegistryRequestDto dto) {
//        return ResponseEntity.ok(registryService.createRegistry(dto));
//    }
}