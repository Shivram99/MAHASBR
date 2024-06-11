package com.mahasbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.MstSubMenu;
import com.mahasbr.repository.MstSubMenuRepository;

@Service
public class MstSubMenuServiceImpl implements MstSubMenuService {
    @Autowired
    private MstSubMenuRepository repository;

    @Override
    public List<MstSubMenu> getAllSubMenus() {
        return repository.findAll();
    }

    @Override
    public MstSubMenu getSubMenuById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public MstSubMenu createSubMenu(MstSubMenu subMenu) {
        return repository.save(subMenu);
    }

    @Override
    public MstSubMenu updateSubMenu(Long id, MstSubMenu subMenu) {
        if (repository.existsById(id)) {
            subMenu.setSubMenuId(id);
            return repository.save(subMenu);
        } else {
            return null; // Or handle the case where the submenu with the given id doesn't exist
        }
    }

    @Override
    public void deleteSubMenu(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<MstSubMenu> getSubMenusByUserRole(Long userRoleId) {
        // Implement your logic to retrieve submenus by user role ID
        return null;
    }}
