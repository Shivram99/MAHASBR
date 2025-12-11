package com.mahasbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mahasbr.entity.NICClassEntity;
import com.mahasbr.entity.NICCodeEntity;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.repository.NICClassRepository;
import com.mahasbr.repository.NICCodeRepository;
@Service
public class NICCodeService {

    @Autowired
    private NICCodeRepository nicCodeRepository;

    @Autowired
    private NICClassRepository nicClassRepository;

    /**
     * Fetch all NIC Codes
     */
    public List<NICCodeEntity> getAllCodes() {
        return nicCodeRepository.findAll();
    }

    /**
     * Fetch a single NIC Code
     */
    public NICCodeEntity getCodeById(Long id) {
        return nicCodeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("NIC Code not found with ID: " + id));
    }

    /**
     * Create NIC Code
     */
    @Transactional
    public NICCodeEntity createCode(NICCodeEntity nicCodeEntity) {

        String classCode = nicCodeEntity.getNicClass().getClassCode();

        NICClassEntity nicClass = nicClassRepository.findById(classCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Class not found with code: " + classCode));

        nicCodeEntity.setNicClass(nicClass);

        if (nicCodeEntity.getIsActive() == null)
            nicCodeEntity.setIsActive("Y");

        return nicCodeRepository.save(nicCodeEntity);
    }

    /**
     * Update NIC Code
     */
    @Transactional
    public NICCodeEntity updateCode(Long id, NICCodeEntity nicCodeEntity) {

        NICCodeEntity existing = getCodeById(id);

        existing.setCode(nicCodeEntity.getCode());
        existing.setDescription(nicCodeEntity.getDescription());

        // If class is updated
        if (nicCodeEntity.getNicClass() != null &&
            nicCodeEntity.getNicClass().getClassCode() != null) {

            String newClassCode = nicCodeEntity.getNicClass().getClassCode();

            NICClassEntity nicClass = nicClassRepository.findById(newClassCode)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Class not found with code: " + newClassCode));

            existing.setNicClass(nicClass);
        }

        return nicCodeRepository.save(existing);
    }

    /**
     * Soft Delete (Toggle Active/Inactive)
     */
    @Transactional
    public NICCodeEntity toggleStatus(Long id) {

        NICCodeEntity existing = getCodeById(id);

        existing.setIsActive(existing.getIsActive().equals("Y") ? "N" : "Y");

        return nicCodeRepository.save(existing);
    }

    /**
     * Hard delete
     */
    @Transactional
    public void deleteCode(Long id) {
        NICCodeEntity existing = getCodeById(id);
        nicCodeRepository.delete(existing);
    }
}

