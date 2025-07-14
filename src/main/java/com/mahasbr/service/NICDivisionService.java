package com.mahasbr.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.config.ResourceNotFoundException;
import com.mahasbr.entity.NICCategoryEntity;
import com.mahasbr.entity.NICDivisionEntity;
import com.mahasbr.repository.NICDivisionRepository;

@Service
public class NICDivisionService {

    @Autowired
    private NICDivisionRepository nicDivisionRepository;

    @Autowired
    private NICCategoryService nicCategoryService;

    public List<NICDivisionEntity> getAllDivisions() {
        return nicDivisionRepository.findAll();
    }

    public NICDivisionEntity getDivisionByCode(String divisionCode) {
        return nicDivisionRepository.findById(divisionCode)
                .orElseThrow(() -> new ResourceNotFoundException("Division not found with code: " + divisionCode));
    }

    public NICDivisionEntity createDivision(NICDivisionEntity nicDivisionEntity) {
        NICCategoryEntity category = nicCategoryService.getCategoryByCode(nicDivisionEntity.getCategory().getCategoryCode());
        nicDivisionEntity.setCategory(category);
        return nicDivisionRepository.save(nicDivisionEntity);
    }

    public NICDivisionEntity updateDivision(String divisionCode, NICDivisionEntity nicDivisionEntity) {
        NICDivisionEntity existingDivision = getDivisionByCode(divisionCode);
        existingDivision.setDescription(nicDivisionEntity.getDescription());
        existingDivision.setCategory(nicCategoryService.getCategoryByCode(nicDivisionEntity.getCategory().getCategoryCode()));
        return nicDivisionRepository.save(existingDivision);
    }

    public void deleteDivision(String divisionCode) {
        NICDivisionEntity existingDivision = getDivisionByCode(divisionCode);
        nicDivisionRepository.delete(existingDivision);
    }
}
