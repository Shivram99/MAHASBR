package com.mahasbr.dto;

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
public class CsvUploadValidationResultDto {
	private boolean valid;
	private boolean duplicate;
	private String errorMessage;
}
