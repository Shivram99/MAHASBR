package com.mahasbr.dto;


import java.io.Serializable;

public class MstSubMenuModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String menuName;
	private String roleName;

	private String subMenuEnglish;
	private String subMenuMarathi;
	private String controllerName;
	private String linkName;
	private String icon;
	
	
	private int subMenuId;
	private int isActive;
	
	private int menuCode;
	private int roleId;
	
	private int subMenuCode; // Sub Menu Key For Edit
	
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getSubMenuEnglish() {
		return subMenuEnglish;
	}
	public void setSubMenuEnglish(String subMenuEnglish) {
		this.subMenuEnglish = subMenuEnglish;
	}
	public String getSubMenuMarathi() {
		return subMenuMarathi;
	}
	public void setSubMenuMarathi(String subMenuMarathi) {
		this.subMenuMarathi = subMenuMarathi;
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
	
	public int getSubMenuId() {
		return subMenuId;
	}
	public void setSubMenuId(int subMenuId) {
		this.subMenuId = subMenuId;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
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
	public int getSubMenuCode() {
		return subMenuCode;
	}
	public void setSubMenuCode(int subMenuCode) {
		this.subMenuCode = subMenuCode;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	
}