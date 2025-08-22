package com.mahasbr.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class TopicModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private int key;
	private String menuName;

	private int SubMenukey;
	private int menuKey;
	private int roleKey;
	private String subMenuName;
	private String controllerName;
	private String linkName;
	private String icon;

}