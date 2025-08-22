package com.mahasbr.repository;

import java.util.List;
import java.util.Set;

import com.mahasbr.entity.Role;

public interface CommonHomeMethodsRepo {

	public List<Object[]> findRoleLevelMstList();

	public List<Object[]> findMenuNameByRoleID(Set<Long> levelRoleVal);

	public List<Object[]> findSubMenuByRoleID(Set<Long> levelRoleVal);

	public List<Object[]> findAllMenu();

	public List<Object[]> findAllRole();

	public List<Object[]> findAllSubMenu();

	public List<Object[]> findAllMenuRoleMapping();

	public List<Object[]> findMenuMappedToUsername(String userName);

	public List<Object[]> findSubMenuByUserName(String userName);

}