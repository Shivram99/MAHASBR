package com.mahasbr.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.config.ResourceNotFoundException;
import com.mahasbr.entity.NICDivisionEntity;
import com.mahasbr.entity.NICGroupEntity;
import com.mahasbr.repository.NICGroupRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NICGroupService {

    @Autowired
    private NICGroupRepository nicGroupRepository;

    @Autowired
    private NICDivisionService nicDivisionService;

    public List<NICGroupEntity> getAllGroups() {
        return nicGroupRepository.findAll();
    }

    public NICGroupEntity getGroupByCode(String groupCode) {
        return nicGroupRepository.findById(groupCode)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with code: " + groupCode));
    }

    public NICGroupEntity createGroup(NICGroupEntity nicGroupEntity) {
        NICDivisionEntity division = nicDivisionService.getDivisionByCode(nicGroupEntity.getDivision().getDivisionCode());
        nicGroupEntity.setDivision(division);
        return nicGroupRepository.save(nicGroupEntity);
    }

    public NICGroupEntity updateGroup(String groupCode, NICGroupEntity nicGroupEntity) {
        NICGroupEntity existingGroup = getGroupByCode(groupCode);
        existingGroup.setDescription(nicGroupEntity.getDescription());
        existingGroup.setDivision(nicDivisionService.getDivisionByCode(nicGroupEntity.getDivision().getDivisionCode()));
        return nicGroupRepository.save(existingGroup);
    }

    public void deleteGroup(String groupCode) {
        NICGroupEntity existingGroup = getGroupByCode(groupCode);
        nicGroupRepository.delete(existingGroup);
    }
}
