package com.mahasbr.dto;


import java.io.Serializable;

public class MstMenuRoleMappingModel implements Serializable {

	private static final long serialVersionUID = 1L;
	

	private int menuMapId;
	private String roleName;
	private String menuName;
	private int isActive;
	
	private int menuCode;
	private int roleId;
	
	
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public int getMenuMapId() {
		return menuMapId;
	}
	public void setMenuMapId(int menuMapId) {
		this.menuMapId = menuMapId;
	}
	public int getMenuCode() {
		return menuCode;
	}
	public void setMenuCode(int menuCode) {
		this.menuCode = menuCode;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	
	
}