package com.mahasbr.dto;

import java.io.Serializable;


public class MstMenuModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String menuNameEnglish;
	private String menuNameMarathi;

	private int menuId;
	private int menuCode;
	private int isActive;
	
	private String icon;
	
	public String getMenuNameEnglish() {
		return menuNameEnglish;
	}
	public void setMenuNameEnglish(String menuNameEnglish) {
		this.menuNameEnglish = menuNameEnglish;
	}
	public String getMenuNameMarathi() {
		return menuNameMarathi;
	}
	public void setMenuNameMarathi(String menuNameMarathi) {
		this.menuNameMarathi = menuNameMarathi;
	}
	
	public int getMenuId() {
		return menuId;
	}
	public void setMenuId(int menuId) {
		this.menuId = menuId;
	}
	public int getMenuCode() {
		return menuCode;
	}
	public void setMenuCode(int menuCode) {
		this.menuCode = menuCode;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	
	
	
}