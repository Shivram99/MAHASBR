package com.mahasbr.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
	MstMenuRoleMappingRepository mstMenuRoleMappingRepository;
	
	@Autowired
	MstMenuRepo mstMenuRepo;
	
	@Autowired
	RoleRepository RoleRepository;

	@Override
	public long saveMenuRoleMapping(MstMenuRoleMappingModel mstMenuRoleMappingModel, User messages) {
		Optional<MstMenu> mstMenu = mstMenuRepo.findById(mstMenuRoleMappingModel.getMstmenu());
		Optional<Role> role = RoleRepository.findById(mstMenuRoleMappingModel.getRoles());
		if (mstMenu.isPresent() && role.isPresent()) {
//			Set<MstMenu> mstMenus = new HashSet<>();
//			Set<Role> roles = new HashSet<>();
//			  mstMenus.add(mstMenu.get());
//			  roles.add(role.get());
		         MstMenuRoleMapping mstMenuRoleMapping = new MstMenuRoleMapping(mstMenuRoleMappingModel.getIsActive());
			mstMenuRoleMappingRepository.save(mstMenuRoleMapping);

		}
		long saveId = mstMenuRoleMappingModel.getMstmenu();

		return saveId;

	}
}
