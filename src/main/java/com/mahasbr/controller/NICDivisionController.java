package com.mahasbr.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.dto.NICDivisionDTO;
import com.mahasbr.entity.NICDivisionEntity;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.service.NICDivisionService;
import com.mahasbr.util.ApiResponse;
@RestController
@RequestMapping("/citizenSearch/api/nic-divisions")
public class NICDivisionController {
	
	private static final Logger logger = LoggerFactory.getLogger(NICDivisionController.class);
	

    @Autowired
    private NICDivisionService divisionService;

    // ✅ Get all divisions (DTO - no child load)
    @GetMapping
    public ResponseEntity<ApiResponse<List<NICDivisionDTO>>> getAllDivisions() {
        List<NICDivisionDTO> divisions = divisionService.getAllDivisions();
        return ResponseEntity.ok(ApiResponse.ok(divisions, "Divisions fetched successfully"));
    }

    // ✅ Get division by code
    @GetMapping("/{divisionCode}")
    public ResponseEntity<ApiResponse<NICDivisionDTO>> getDivisionByCode(@PathVariable String divisionCode) {
        try {
            NICDivisionDTO division = divisionService.getDivisionByCode(divisionCode);
            return ResponseEntity.ok(ApiResponse.ok(division, "Division fetched successfully"));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail("Division not found: " + divisionCode));
        }
    }

    // ✅ Create new division
    @PostMapping
    public ResponseEntity<ApiResponse<NICDivisionEntity>> createDivision(@RequestBody NICDivisionDTO dto) {
        NICDivisionEntity createdDivision = divisionService.createDivision(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(createdDivision, "Division created successfully"));
    }

    // ✅ Update division
    @PutMapping("/{divisionCode}")
    public ResponseEntity<ApiResponse<NICDivisionEntity>> updateDivision(
            @PathVariable String divisionCode,
            @RequestBody NICDivisionDTO dto) {

        NICDivisionEntity updatedDivision = divisionService.updateDivision(divisionCode, dto);
        return ResponseEntity.ok(ApiResponse.ok(updatedDivision, "Division updated successfully"));
    }

    // ❌ Removed Hard DELETE
    // ✅ Added Soft Delete / Toggle Active Status
    @PatchMapping("/{divisionCode}/toggle-status")
    public ResponseEntity<ApiResponse<NICDivisionEntity>> toggleDivisionStatus(@PathVariable String divisionCode) {
        NICDivisionEntity updated = divisionService.toggleStatus(divisionCode);
        String message = updated.getIsActive().equals("Y")
                ? "Division activated successfully"
                : "Division deactivated successfully";

        return ResponseEntity.ok(ApiResponse.ok(updated, message));
    }
}
