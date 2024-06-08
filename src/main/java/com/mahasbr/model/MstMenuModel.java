package com.mahasbr.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class MstMenuModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long menuCode;
	private String menuNameEnglish;
	private String menuNameMarathi;
	private String isActive;

}
