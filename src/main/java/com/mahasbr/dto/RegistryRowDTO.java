package com.mahasbr.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistryRowDTO {
	private Map<String, String> rowData;
	private boolean valid;
	private String errorMessage;
	private int rowNumber;
}