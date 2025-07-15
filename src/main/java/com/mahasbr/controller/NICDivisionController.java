package com.mahasbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.mahasbr.entity.NICDivisionEntity;
import com.mahasbr.service.NICDivisionService;
import com.mahasbr.util.ApiResponse;

@RestController
@RequestMapping("/api/nic-divisions")
public class NICDivisionController {

    @Autowired
    private NICDivisionService nicDivisionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NICDivisionEntity>>> getAllDivisions() {
        List<NICDivisionEntity> divisions = nicDivisionService.getAllDivisions();
        return new ResponseEntity<>(new ApiResponse<>("Divisions fetched successfully", divisions), HttpStatus.OK);
    }

    @GetMapping("/{divisionCode}")
    public ResponseEntity<ApiResponse<NICDivisionEntity>> getDivisionByCode(@PathVariable String divisionCode) {
        NICDivisionEntity division = nicDivisionService.getDivisionByCode(divisionCode);
        return new ResponseEntity<>(new ApiResponse<>("Division fetched successfully", division), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NICDivisionEntity>> createDivision(@RequestBody NICDivisionEntity nicDivisionEntity) {
        NICDivisionEntity createdDivision = nicDivisionService.createDivision(nicDivisionEntity);
        return new ResponseEntity<>(new ApiResponse<>("Division created successfully", createdDivision), HttpStatus.CREATED);
    }

    @PutMapping("/{divisionCode}")
    public ResponseEntity<ApiResponse<NICDivisionEntity>> updateDivision(@PathVariable String divisionCode, @RequestBody NICDivisionEntity nicDivisionEntity) {
        NICDivisionEntity updatedDivision = nicDivisionService.updateDivision(divisionCode, nicDivisionEntity);
        return new ResponseEntity<>(new ApiResponse<>("Division updated successfully", updatedDivision), HttpStatus.OK);
    }

    @DeleteMapping("/{divisionCode}")
    public ResponseEntity<ApiResponse<Void>> deleteDivision(@PathVariable String divisionCode) {
        nicDivisionService.deleteDivision(divisionCode);
        return new ResponseEntity<>(new ApiResponse<>("Division deleted successfully", null), HttpStatus.NO_CONTENT);
    }
}