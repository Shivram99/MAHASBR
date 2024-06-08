package com.mahasbr.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MstMenuRoleMappingModel {
	
	long mstMenu;
	long roles;
	private Character isActive;

}
