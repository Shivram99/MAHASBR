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
public class CsvUploadSuccessPageResponse {
	private String jobId;
	private int pageNumber;
	private int pageSize;
	private int totalPages;
	private int totalRecords;
	private List<CsvUploadSuccessRecordDto> records;
}
