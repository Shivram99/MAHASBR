package com.mahasbr.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO {
	private Long id;
	private String nameEn;
	private String nameMr;
	private String route;
	private String icon;
	private Integer sequence;
	private Boolean active;
	private Long parentId; 
	 private String menuType;
	private List<MenuDTO> children = new ArrayList<>();
}
