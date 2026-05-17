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
public class CsvUploadPreviewResponse {
	private String jobId;
	private String fileName;
	private long fileSize;
	private String status;
	private String message;
	private int totalRecords;
	private int validRecords;
	private int invalidRecords;
	private int duplicateRecords;
	private int pageNumber;
	private int pageSize;
	private int totalPages;
	private boolean previewReady;
	private List<CsvUploadRecordDto> records;
}
