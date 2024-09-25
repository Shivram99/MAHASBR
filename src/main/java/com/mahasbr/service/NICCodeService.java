package com.mahasbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.config.ResourceNotFoundException;
import com.mahasbr.entity.NICClassEntity;
import com.mahasbr.entity.NICCodeEntity;
import com.mahasbr.repository.NICCodeRepository;

@Service
public class NICCodeService {

    @Autowired
    private NICCodeRepository nicCodeRepository;

    @Autowired
    private NICClassService nicClassService;

    public List<NICCodeEntity> getAllCodes() {
        return nicCodeRepository.findAll();
    }

    public NICCodeEntity getCodeById(Long id) {
        return nicCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Code not found with ID: " + id));
    }

    public NICCodeEntity createCode(NICCodeEntity nicCodeEntity) {
        NICClassEntity nicClass = nicClassService.getClassByCode(nicCodeEntity.getNicClass().getClassCode());
        nicCodeEntity.setNicClass(nicClass);
        return nicCodeRepository.save(nicCodeEntity);
    }

    public NICCodeEntity updateCode(Long id, NICCodeEntity nicCodeEntity) {
        NICCodeEntity existingCode = getCodeById(id);
        existingCode.setCode(nicCodeEntity.getCode());
        existingCode.setDescription(nicCodeEntity.getDescription());
        existingCode.setNicClass(nicClassService.getClassByCode(nicCodeEntity.getNicClass().getClassCode()));
        return nicCodeRepository.save(existingCode);
    }

    public void deleteCode(Long id) {
        NICCodeEntity existingCode = getCodeById(id);
        nicCodeRepository.delete(existingCode);
    }
}
