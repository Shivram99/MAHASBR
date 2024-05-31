package com.mahasbr.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MstMenuRoleMappingModel {
	
	long mstmenu;
	long roles;
	private Character isActive;

}
