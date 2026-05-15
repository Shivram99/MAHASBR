package com.mahasbr.service;

import java.util.Locale;

import org.springframework.http.MediaType;

public enum RegisteredEstablishmentExportFormat {

	PDF("pdf", MediaType.APPLICATION_PDF_VALUE),
	EXCEL("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

	private final String fileExtension;
	private final String mediaType;

	RegisteredEstablishmentExportFormat(String fileExtension, String mediaType) {
		this.fileExtension = fileExtension;
		this.mediaType = mediaType;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public String getMediaType() {
		return mediaType;
	}

	public static RegisteredEstablishmentExportFormat fromValue(String value) {
		if (value == null) {
			throw new IllegalArgumentException("Export format is required.");
		}

		return RegisteredEstablishmentExportFormat.valueOf(value.trim().toUpperCase(Locale.ROOT));
	}
}
