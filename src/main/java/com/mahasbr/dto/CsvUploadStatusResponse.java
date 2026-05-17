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
public class CsvUploadStatusResponse {
	private String jobId;
	private String fileName;
	private long fileSize;
	private String status;
	private String message;
	private int progressPercentage;
	private int totalRecords;
	private int processedRecords;
	private int successRecords;
	private int failedRecords;
	private int pendingRecords;
	private int validRecords;
	private int invalidRecords;
	private int duplicateRecords;
	private int totalPreviewPages;
	private int totalSuccessPages;
	private int totalFailedPages;
	private boolean previewReady;
	private boolean canStart;
	private boolean canPause;
	private boolean canResume;
	private boolean canCancel;
}
