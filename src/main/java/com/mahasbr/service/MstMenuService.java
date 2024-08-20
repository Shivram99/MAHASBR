package com.mahasbr.service;

import java.util.List;

import com.mahasbr.entity.MstMenu;
import com.mahasbr.entity.User;
import com.mahasbr.model.MstMenuModel;

public interface MstMenuService {

	public List<MstMenu> getAllMenus();

	public MstMenu getMenuById(Long id);

	public MstMenu createMenu(MstMenu menu);

	public MstMenu updateMenu(Long id, MstMenu menu);

	public void deleteMenu(Long id);
	
	public List<MstMenu> getMenusByUserId(Long userId);
}
