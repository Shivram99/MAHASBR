package com.mahasbr.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuDTO {
	private Long id;
	private String nameEn;
	private String nameMh;
	private String url;
	private Integer menuOrder;
	private Boolean active;
	private Long parentId;
	private List<Long> roleIds; // NEW FIELD
	private List<MenuDTO> children;
}