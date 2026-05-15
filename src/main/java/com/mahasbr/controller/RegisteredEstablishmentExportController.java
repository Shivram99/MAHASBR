package com.mahasbr.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.service.RegisteredEstablishmentExportService;

@RestController
@RequestMapping("/api/registered-establishments/export")
public class RegisteredEstablishmentExportController {

	private static final Logger logger = LoggerFactory.getLogger(RegisteredEstablishmentExportController.class);
	private static final DateTimeFormatter FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

	private final RegisteredEstablishmentExportService registeredEstablishmentExportService;

	public RegisteredEstablishmentExportController(
			RegisteredEstablishmentExportService registeredEstablishmentExportService) {
		this.registeredEstablishmentExportService = registeredEstablishmentExportService;
	}

	@GetMapping(value = "/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public ResponseEntity<byte[]> exportRegisteredEstablishmentsExcel(
			@RequestParam(name = "districtId", required = false) List<Long> districtIds,
			@RequestParam(name = "talukaId", required = false) List<Long> talukaIds,
			@RequestParam(name = "brn", required = false) String brn) {
		logger.info(
				"Received registered establishments Excel export request. districtIds={} talukaIds={} brnPresent={}",
				districtIds, talukaIds, StringUtils.hasText(brn));

		String fileName = "registered-establishments-" + FILE_NAME_FORMATTER.format(LocalDateTime.now()) + ".xlsx";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		registeredEstablishmentExportService.exportRegisteredEstablishmentsExcel(districtIds, talukaIds, brn,
				outputStream);
		byte[] fileContent = outputStream.toByteArray();

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.contentLength(fileContent.length)
				.header(HttpHeaders.CONTENT_DISPOSITION,
						ContentDisposition.attachment().filename(fileName).build().toString())
				.body(fileContent);
	}
}
