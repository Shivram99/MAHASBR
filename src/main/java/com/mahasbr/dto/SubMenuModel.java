package com.mahasbr.dto;

import java.io.Serializable;

public class SubMenuModel  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int subMenuId;
	private int menuCode;
	private int roleId;
	private String subMenuName;
	private String controllerName;
	private String linkName;
	
	
	public int getSubMenuId() {
		return subMenuId;
	}
	public void setSubMenuId(int subMenuId) {
		this.subMenuId = subMenuId;
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
	public String getSubMenuName() {
		return subMenuName;
	}
	public void setSubMenuName(String subMenuName) {
		this.subMenuName = subMenuName;
	}
	public String getControllerName() {
		return controllerName;
	}
	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}
	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
}