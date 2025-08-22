package com.mahasbr.service;

import java.util.List;
import java.util.Set;

import com.mahasbr.dto.MstMenuModel;
import com.mahasbr.dto.MstMenuRoleMappingModel;
import com.mahasbr.dto.MstRoleModel;
import com.mahasbr.dto.MstSubMenuModel;
import com.mahasbr.dto.TopicModel;
import com.mahasbr.entity.Role;

import jakarta.validation.Valid;

public interface CommonHomeMethodsService {

//	public List<TopicModel> findMenuNameByRoleID(int levelRoleVal, String lang);

	public List<TopicModel> findMenuNameByRoleID(Set<Role> levelRoleVal, String lang);

	public List<TopicModel> findSubMenuByRoleID(Set<Role> levelRoleVal, String lang);

	public List<MstMenuModel> findAllMenu(String language);

	public List<MstRoleModel> findAllRole(String language);

	public List<MstSubMenuModel> findAllSubMenu(String language);

	public List<MstMenuRoleMappingModel> findAllMenuRoleMapping(String language);

	public List<TopicModel> findMenuMappedToUsername(String userName);

	public List<TopicModel> findSubMenuByUserName(String userName);

}
