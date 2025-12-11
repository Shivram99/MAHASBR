package com.mahasbr.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mahasbr.dto.NICClassDTO;
import com.mahasbr.entity.NICClassEntity;
import com.mahasbr.entity.NICGroupEntity;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.repository.NICClassRepository;
import com.mahasbr.repository.NICGroupRepository;
@Service
public class NICClassService {

    @Autowired
    private NICClassRepository nicClassRepository;

    @Autowired
    private NICGroupRepository nicGroupRepository;

    // ------------------ GET ALL ------------------

    public List<NICClassDTO> getAllClasses() {
        return nicClassRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ------------------ FILTER BY GROUP ------------------

    public List<NICClassDTO> getClassesByGroup(String groupCode) {
        return nicClassRepository.findByGroup_GroupCode(groupCode)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ------------------ GET BY CODE ------------------

    public NICClassDTO getClassByCode(String classCode) {
        NICClassEntity entity = nicClassRepository.findById(classCode)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with code: " + classCode));

        return toDto(entity);
    }

    // ------------------ CREATE ------------------

    @Transactional
    public NICClassDTO createClass(NICClassDTO dto) {

        NICGroupEntity group = nicGroupRepository.findById(dto.getGroupCode())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found with code: " + dto.getGroupCode()));

        NICClassEntity entity = new NICClassEntity();
        entity.setClassCode(dto.getClassCode());
        entity.setDescription(dto.getDescription());
        entity.setGroup(group);
        entity.setIsActive(dto.getIsActive() == null ? "Y" : dto.getIsActive());

        NICClassEntity saved = nicClassRepository.save(entity);

        return toDto(saved);
    }

    // ------------------ UPDATE ------------------

    @Transactional
    public NICClassDTO updateClass(String classCode, NICClassDTO dto) {

        NICClassEntity existing = nicClassRepository.findById(classCode)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with code: " + classCode));

        existing.setDescription(dto.getDescription());

        // 🔥 Corrected mapping: Use groupCode to fetch group
        if (dto.getGroupCode() != null) {
            NICGroupEntity group = nicGroupRepository.findById(dto.getGroupCode())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Group not found with code: " + dto.getGroupCode()));

            existing.setGroup(group);
        }

        NICClassEntity updated = nicClassRepository.save(existing);

        return toDto(updated);
    }

    // ------------------ TOGGLE ACTIVE ------------------

    @Transactional
    public NICClassDTO toggleStatus(String classCode) {

        NICClassEntity existing = nicClassRepository.findById(classCode)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with code: " + classCode));

        existing.setIsActive(existing.getIsActive().equals("Y") ? "N" : "Y");

        NICClassEntity updated = nicClassRepository.save(existing);

        return toDto(updated);
    }

    // ------------------ DELETE ------------------

    @Transactional
    public void deleteClass(String classCode) {
        NICClassEntity existing = nicClassRepository.findById(classCode)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with code: " + classCode));

        nicClassRepository.delete(existing);
    }

    // ------------------ DTO MAPPER ------------------

    private NICClassDTO toDto(NICClassEntity e) {
        NICClassDTO dto = new NICClassDTO();

        dto.setClassCode(e.getClassCode());
        dto.setDescription(e.getDescription());
        dto.setIsActive(e.getIsActive());
        dto.setGroupCode(e.getGroup() != null ? e.getGroup().getGroupCode() : null);

        return dto;
    }
}

