package com.mahasbr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MenuRequestDto {

	@NotNull(message = "Menu Code is required")
	private Integer menuCode;

	@NotBlank(message = "Menu name in English is required")
	private String menuNameEnglish;

	@NotBlank(message = "Menu name in Marathi is required")
	private String menuNameMarathi;

	@NotBlank(message = "isActive flag is required")
	private String isActive;

	private String icon;
}
