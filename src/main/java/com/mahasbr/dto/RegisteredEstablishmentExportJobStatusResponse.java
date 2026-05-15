package com.mahasbr.dto;

public record RegisteredEstablishmentExportJobStatusResponse(
		String jobId,
		String status,
		String stage,
		int progressPercent,
		String format,
		String fileName,
		Long totalRows,
		Long processedRows,
		String message,
		boolean downloadReady) {
}
