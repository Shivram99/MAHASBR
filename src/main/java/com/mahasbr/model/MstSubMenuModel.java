package com.mahasbr.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MstSubMenuModel {

	private long menuId;
	private long roleId;
	private String subMenuNameEnglish;
	private String subMenuNameMarathi;
	private String controllerName;
	private String linkName;
	private Character isActive;

}
