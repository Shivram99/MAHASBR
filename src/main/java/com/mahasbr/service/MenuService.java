package com.mahasbr.service;

import java.util.List;
import java.util.Set;

import com.mahasbr.dto.MenuCreateDTO;
import com.mahasbr.dto.MenuDTO;
import com.mahasbr.entity.Role;

public interface MenuService {
	 List<MenuDTO> getAllMenus();

	    MenuDTO getMenuById(Long id);

	    MenuDTO createMenu(MenuCreateDTO dto);

	    MenuDTO updateMenu(Long id, MenuCreateDTO dto);

	    void deleteMenu(Long id);

	    // Role Based
	    List<MenuDTO> getMenusForRole(Role role);

	    void assignMenuToRole(Long menuId, Long roleId);

	    void removeMenuFromRole(Long menuId, Long roleId);

		List<MenuDTO> getMenusForRoles(Set<String> roleNames);
}
