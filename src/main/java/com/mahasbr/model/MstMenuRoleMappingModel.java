package com.mahasbr.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MstMenuRoleMappingModel {
	
	long menuId;
	long roleId;
	private String isActive;
	String menuMapID;

}
