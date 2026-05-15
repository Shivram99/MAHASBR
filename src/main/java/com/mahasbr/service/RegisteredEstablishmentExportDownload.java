package com.mahasbr.service;

import java.nio.file.Path;

public record RegisteredEstablishmentExportDownload(
		Path path,
		String fileName,
		String mediaType,
		long fileSize) {
}
