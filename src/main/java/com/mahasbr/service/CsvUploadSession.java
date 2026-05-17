package com.mahasbr.service;

import java.time.LocalDateTime;

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
public class CsvUploadSession {
	private String uploadId;
	private String fileName;
	private long fileSize;
	private String storedFileName;
	private CsvUploadLifecycleStatus status;
	private String message;
	private int previewPageSize;
	private int resultPageSize;
	private int totalRecords;
	private int validRecords;
	private int invalidRecords;
	private int duplicateRecords;
	private int processedRecords;
	private int insertedRecords;
	private int failedRecords;
	private int progressPercentage;
	private int totalPreviewPages;
	private int totalSuccessPages;
	private int totalFailedPages;
	private boolean previewReady;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
