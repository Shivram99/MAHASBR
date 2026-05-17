package com.mahasbr.dto;

import java.util.List;

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
public class CsvUploadProcessResponse {
	private int totalRecords;
	private int insertedRecords;
	private int failedRecords;
	private int duplicateRecords;
	private List<CsvUploadSuccessRecordDto> successRecords;
	private List<CsvUploadFailedRecordDto> failedRecordsList;
}
