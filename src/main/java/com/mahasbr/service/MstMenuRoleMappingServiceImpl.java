package com.mahasbr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.MstMenu;
import com.mahasbr.entity.MstMenuRoleMapping;
import com.mahasbr.entity.Role;
import com.mahasbr.entity.User;
import com.mahasbr.model.MstMenuRoleMappingModel;
import com.mahasbr.repository.MstMenuRepo;
import com.mahasbr.repository.MstMenuRoleMappingRepository;
import com.mahasbr.repository.RoleRepository;

@Service
public class MstMenuRoleMappingServiceImpl implements MstMenuRoleMappingService {

    @Autowired
    private MstMenuRoleMappingRepository repository;

    public List<MstMenuRoleMapping> getAllMappings() {
        return repository.findAll();
    }

    public MstMenuRoleMapping getMappingById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public MstMenuRoleMapping createMapping(MstMenuRoleMapping mapping) {
        return repository.save(mapping);
    }

    public MstMenuRoleMapping updateMapping(Long id, MstMenuRoleMapping mapping) {
        if (repository.existsById(id)) {
            mapping.setMenuMapID(id);
            return repository.save(mapping);
        } else {
            return null; // Or handle the case where the mapping with the given id doesn't exist
        }
    }

    public void deleteMapping(Long id) {
        repository.deleteById(id);
    }
}
