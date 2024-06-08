package com.mahasbr.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.MstMenu;
import com.mahasbr.entity.MstSubMenu;
import com.mahasbr.entity.Role;
import com.mahasbr.entity.User;
import com.mahasbr.model.MstSubMenuModel;
import com.mahasbr.repository.MstMenuRepo;
import com.mahasbr.repository.MstSubMenuRepository;
import com.mahasbr.repository.RoleRepository;

@Service
public class MstSubMenuServiceImpl implements MstSubMenuService {
	@Autowired
	MstMenuRepo mstMenuRepo;

	@Autowired
	RoleRepository RoleRepository;
	@Autowired
	MstSubMenuRepository mstSubMenuRepository;

	@Override
	public long saveSubMenu(MstSubMenuModel mstSubMenuModel, User messages) {
		Optional<MstMenu> mstMenu = mstMenuRepo.findById(mstSubMenuModel.getMenuId());
		Optional<Role> role = RoleRepository.findById(mstSubMenuModel.getRoleId());
		if (mstMenu.isPresent() && role.isPresent()) {
			Set<MstMenu> mstMenus = new HashSet<>();
			Set<Role> roles = new HashSet<>();
			mstMenus.add(mstMenu.get());
			roles.add(role.get());
			MstSubMenu mstSubMenu = new MstSubMenu();
		//	mstSubMenu.setMstmenu(mstMenus);
//			mstSubMenu.setRole(roles);
//			mstSubMenu.setMstmenu(mstMenu.get());
//			mstSubMenu.setRole(role.get());
			mstSubMenu.setSubMenuNameEnglish(mstSubMenuModel.getSubMenuEnglish());
			mstSubMenu.setSubMenuNameMarathi(mstSubMenuModel.getSubMenuMarathi());
			mstSubMenu.setControllerName(mstSubMenuModel.getControllerName());
			mstSubMenu.setLinkName(mstSubMenuModel.getLinkName());
			mstSubMenu.setIsActive('1');

			mstSubMenuRepository.save(mstSubMenu);

		}
		long saveId = mstSubMenuModel.getMenuId();
		return saveId;
	}

}
