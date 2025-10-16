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

import com.mahasbr.dto.DivisionDto;
import com.mahasbr.service.DivisionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/divisions")
@RequiredArgsConstructor
public class DivisionController {

    private final DivisionService divisionService;

    @PostMapping
    public ResponseEntity<DivisionDto> createDivision(@RequestBody @Valid DivisionDto divisionDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(divisionService.createDivision(divisionDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DivisionDto> updateDivision(@PathVariable Long id, @RequestBody @Valid DivisionDto divisionDto) {
        return ResponseEntity.ok(divisionService.updateDivision(id, divisionDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDivision(@PathVariable Long id) {
        divisionService.deleteDivision(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DivisionDto> getDivision(@PathVariable Long id) {
        return ResponseEntity.ok(divisionService.getDivisionById(id));
    }

    @GetMapping
    public ResponseEntity<List<DivisionDto>> getAllDivisions() {
        return ResponseEntity.ok(divisionService.getAllDivisions());
    }
}
