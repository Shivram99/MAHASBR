package com.mahasbr.dto;

public record RegisteredEstablishmentExportJobCreatedResponse(
		String jobId,
		String status,
		String stage,
		int progressPercent,
		String format,
		String fileName) {
}
