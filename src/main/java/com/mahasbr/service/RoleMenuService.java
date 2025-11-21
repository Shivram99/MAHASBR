package com.mahasbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.Menu;
import com.mahasbr.entity.Role;
import com.mahasbr.entity.RoleMenu;
import com.mahasbr.repository.MenuRepository;
import com.mahasbr.repository.RoleMenuRepository;
import com.mahasbr.repository.RoleRepository;

@Service
public class RoleMenuService {
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private RoleMenuRepository roleMenuRepository;
	
	@Autowired
	private MenuRepository menuRepository;
	
	public void assignMenusToRole(Long roleId, List<Long> menuIds) {

	    Role role = roleRepository.findById(roleId)
	            .orElseThrow(() -> new RuntimeException("Role not found"));

	    // Remove old menus
	    roleMenuRepository.deleteByRole(role);

	    // Assign new menus
	    for (Long menuId : menuIds) {
	        Menu menu = menuRepository.findById(menuId)
	                .orElseThrow(() -> new RuntimeException("Menu not found: " + menuId));

	        RoleMenu rm = new RoleMenu();
	        rm.setRole(role);
	        rm.setMenu(menu);

	        roleMenuRepository.save(rm);
	    }
	}

}
