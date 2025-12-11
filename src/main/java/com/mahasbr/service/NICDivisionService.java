package com.mahasbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.dto.NICDivisionDTO;
import com.mahasbr.entity.NICCategoryEntity;
import com.mahasbr.entity.NICDivisionEntity;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.repository.NICCategoryRepository;
import com.mahasbr.repository.NICDivisionRepository;
@Service
public class NICDivisionService {

    @Autowired
    private NICDivisionRepository divisionRepo;

    @Autowired
    private NICCategoryRepository categoryRepo;

    // ✅ Get all divisions — return DTOs (no child entities)
    public List<NICDivisionDTO> getAllDivisions() {
        return divisionRepo.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    // ✅ Get by division code — return DTO
    public NICDivisionDTO getDivisionByCode(String divisionCode) {
        NICDivisionEntity division = divisionRepo.findById(divisionCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Division not found with code: " + divisionCode
                ));
        return convertToDTO(division);
    }

    // ✅ Create Division (DTO → Entity mapping)
    public NICDivisionEntity createDivision(NICDivisionDTO dto) {

        NICCategoryEntity category = categoryRepo.findById(dto.getCategoryCode())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with code: " + dto.getCategoryCode()
                        )
                );

        NICDivisionEntity division = new NICDivisionEntity();
        division.setDivisionCode(dto.getDivisionCode());
        division.setDescription(dto.getDescription());
        division.setCategory(category);
        division.setIsActive("Y");

        return divisionRepo.save(division);
    }

    // ✅ Update Division
    public NICDivisionEntity updateDivision(String divisionCode, NICDivisionDTO dto) {

        NICDivisionEntity existing = divisionRepo.findById(divisionCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Division not found: " + divisionCode
                ));

        existing.setDescription(dto.getDescription());

        // Validate new category if provided
        if (dto.getCategoryCode() != null) {
            NICCategoryEntity category = categoryRepo.findById(dto.getCategoryCode())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Category not found: " + dto.getCategoryCode()
                            ));
            existing.setCategory(category);
        }

        return divisionRepo.save(existing);
    }

    // ❌ REMOVE HARD DELETE (not allowed)
    // ✅ SOFT DELETE (status toggle)
    public NICDivisionEntity toggleStatus(String divisionCode) {
        NICDivisionEntity division = divisionRepo.findById(divisionCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Division not found: " + divisionCode
                ));

        division.setIsActive(division.getIsActive().equals("Y") ? "N" : "Y");
        return divisionRepo.save(division);
    }

    // 🔄 Convert entity → DTO
    private NICDivisionDTO convertToDTO(NICDivisionEntity division) {
        NICDivisionDTO dto = new NICDivisionDTO();
        dto.setDivisionCode(division.getDivisionCode());
        dto.setDescription(division.getDescription());
        dto.setCategoryCode(division.getCategory().getCategoryCode());
        dto.setIsActive(division.getIsActive());
        return dto;
    }
}
