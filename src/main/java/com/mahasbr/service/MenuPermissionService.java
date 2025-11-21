package com.mahasbr.service;

import java.util.List;

import com.mahasbr.dto.AssignMenuDTO;
import com.mahasbr.dto.MenuDTO;

public interface MenuPermissionService {
	 void assignMenuToRole(AssignMenuDTO dto);
	    void removeMenuFromRole(AssignMenuDTO dto);

	    void assignMenuToUser(AssignMenuDTO dto);
	    void removeMenuFromUser(AssignMenuDTO dto);

	    List<MenuDTO> getMenusForRole(Long roleId);
	    List<MenuDTO> getMenusForUser(Long userId);
}
