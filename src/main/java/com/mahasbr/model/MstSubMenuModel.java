package com.mahasbr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MstSubMenuModel {

	private long menuId;
	private long roleId;
	private String subMenuEnglish;
	private String subMenuMarathi;
	private String controllerName;
	private String linkName;

	private Character isActive;

}
