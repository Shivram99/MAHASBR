package com.mahasbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.NICCategoryEntity;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.repository.NICCategoryRepository;

@Service
public class NICCategoryService {

    @Autowired
    private NICCategoryRepository nicCategoryRepository;

    public List<NICCategoryEntity> getAllCategories() {
        return nicCategoryRepository.findAll();
    }

    public NICCategoryEntity getCategoryByCode(String categoryCode) {
        return nicCategoryRepository.findById(categoryCode)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with code: " + categoryCode));
    }

    public NICCategoryEntity createCategory(NICCategoryEntity nicCategoryEntity) {
        return nicCategoryRepository.save(nicCategoryEntity);
    }

    public NICCategoryEntity updateCategory(String categoryCode, NICCategoryEntity nicCategoryEntity) {
        NICCategoryEntity existingCategory = getCategoryByCode(categoryCode);
        existingCategory.setDescription(nicCategoryEntity.getDescription());
        return nicCategoryRepository.save(existingCategory);
    }

    public void deleteCategory(String categoryCode) {
        NICCategoryEntity existingCategory = getCategoryByCode(categoryCode);
        nicCategoryRepository.delete(existingCategory);
    }
}
