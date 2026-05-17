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
public class CsvUploadSuccessRecordDto {
	private int rowNumber;
	private String brn;
	private String establishmentName;
	private String rawData;
}
