package com.mahasbr.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.Role;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.repository.RoleRepository;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role saveOrUpdateRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Optional<Role> findRoleById(Long id) {
        return roleRepository.findById(id);
    }


    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));
    }

    public Role createRole(Role role) {

        if (roleRepository.existsByNameIgnoreCase(role.getName()))
            throw new RuntimeException("Role already exists: " + role.getName());

        return roleRepository.save(role);
    }

    public Role updateRole(Long id, Role roleDetails) {

        Role existing = getRoleById(id);

        // Prevent duplicate role names
        if (!existing.getName().equalsIgnoreCase(roleDetails.getName()) &&
            roleRepository.existsByNameIgnoreCase(roleDetails.getName())) {
            throw new RuntimeException("Role already exists: " + roleDetails.getName());
        }

        existing.setName(roleDetails.getName());

        return roleRepository.save(existing);
    }

    public void deleteRole(Long id) {
        Role existing = getRoleById(id);
        roleRepository.delete(existing);
    }
}