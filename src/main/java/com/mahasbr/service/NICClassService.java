package com.mahasbr.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.config.ResourceNotFoundException;
import com.mahasbr.entity.NICClassEntity;
import com.mahasbr.entity.NICGroupEntity;
import com.mahasbr.repository.NICClassRepository;

@Service
public class NICClassService {

    @Autowired
    private NICClassRepository nicClassRepository;

    @Autowired
    private NICGroupService nicGroupService;

    public List<NICClassEntity> getAllClasses() {
        return nicClassRepository.findAll();
    }

    public NICClassEntity getClassByCode(String classCode) {
        return nicClassRepository.findById(classCode)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with code: " + classCode));
    }

    public NICClassEntity createClass(NICClassEntity nicClassEntity) {
        NICGroupEntity group = nicGroupService.getGroupByCode(nicClassEntity.getGroup().getGroupCode());
        nicClassEntity.setGroup(group); 
        return nicClassRepository.save(nicClassEntity);
    }

    public NICClassEntity updateClass(String classCode, NICClassEntity nicClassEntity) {
        NICClassEntity existingClass = getClassByCode(classCode);
        existingClass.setDescription(nicClassEntity.getDescription());
        existingClass.setGroup(nicGroupService.getGroupByCode(nicClassEntity.getClassCode())); 
        return nicClassRepository.save(existingClass);
    }

    public void deleteClass(String classCode) {
        NICClassEntity existingClass = getClassByCode(classCode);
        nicClassRepository.delete(existingClass);
    }
}
