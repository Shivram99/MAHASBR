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
public class CsvUploadFailedRecordDto {
	private int rowNumber;
	private String establishmentName;
	private String brn;
	private String errorReason;
	private String rawData;
}
