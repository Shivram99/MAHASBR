package com.mahasbr.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mahasbr.dto.NICGroupDTO;
import com.mahasbr.entity.NICDivisionEntity;
import com.mahasbr.entity.NICGroupEntity;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.repository.NICDivisionRepository;
import com.mahasbr.repository.NICGroupRepository;

@Service
public class NICGroupService {

    @Autowired
    private NICGroupRepository groupRepo;

    @Autowired
    private NICDivisionRepository divisionRepo;

    /**
     * Get all groups (DTO without lazy loading issues)
     */
    public List<NICGroupDTO> getAllGroups() {
        return groupRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get filtered groups by division
     */
    public List<NICGroupDTO> getGroupsByDivision(String divisionCode) {
        return groupRepo.findByDivision_DivisionCode(divisionCode)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get single group (DTO)
     */
    public NICGroupDTO getGroupByCode(String groupCode) {
        NICGroupEntity entity = groupRepo.findById(groupCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found with code: " + groupCode));

        return toDto(entity);
    }

    /**
     * Create group
     */
    @Transactional
    public NICGroupEntity createGroup(NICGroupDTO dto) {

        NICDivisionEntity division = divisionRepo.findById(dto.getDivisionCode())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Division not found with code: " + dto.getDivisionCode()));

        NICGroupEntity entity = new NICGroupEntity();
        entity.setGroupCode(dto.getGroupCode());
        entity.setDescription(dto.getDescription());
        entity.setDivision(division);
        entity.setIsActive(dto.getIsActive() == null ? "Y" : dto.getIsActive());

        return groupRepo.save(entity);
    }

    /**
     * Update group
     */
    @Transactional
    public NICGroupEntity updateGroup(String groupCode, NICGroupDTO dto) {

        NICGroupEntity existing = groupRepo.findById(groupCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found with code: " + groupCode));

        existing.setDescription(dto.getDescription());

        // Update division if changed
        if (dto.getDivisionCode() != null && !dto.getDivisionCode().isBlank()) {
            NICDivisionEntity division = divisionRepo.findById(dto.getDivisionCode())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Division not found with code: " + dto.getDivisionCode()));

            existing.setDivision(division);
        }

        return groupRepo.save(existing);
    }

    /**
     * Toggle Active/Inactive
     */
    @Transactional
    public NICGroupEntity toggleStatus(String groupCode) {

        NICGroupEntity existing = groupRepo.findById(groupCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found with code: " + groupCode));

        existing.setIsActive(existing.getIsActive().equals("Y") ? "N" : "Y");

        return groupRepo.save(existing);
    }

    /**
     * Delete group (Hard delete)
     */
    @Transactional
    public void deleteGroup(String groupCode) {
        NICGroupEntity existing = groupRepo.findById(groupCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found with code: " + groupCode));

        groupRepo.delete(existing);
    }

    // ------------------ DTO Mapper ------------------

    private NICGroupDTO toDto(NICGroupEntity e) {
        NICGroupDTO dto = new NICGroupDTO();
        dto.setGroupCode(e.getGroupCode());
        dto.setDescription(e.getDescription());
        dto.setIsActive(e.getIsActive());
        dto.setDivisionCode(e.getDivision() != null ? e.getDivision().getDivisionCode() : null);
        return dto;
    }
}
