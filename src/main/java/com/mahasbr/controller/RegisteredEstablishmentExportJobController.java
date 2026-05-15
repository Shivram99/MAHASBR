package com.mahasbr.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahasbr.dto.RegisteredEstablishmentExportJobCreatedResponse;
import com.mahasbr.dto.RegisteredEstablishmentExportJobRequest;
import com.mahasbr.dto.RegisteredEstablishmentExportJobStatusResponse;
import com.mahasbr.service.RegisteredEstablishmentExportDownload;
import com.mahasbr.service.RegisteredEstablishmentExportFormat;
import com.mahasbr.service.RegisteredEstablishmentExportJobFacade;

@RestController
@RequestMapping("/api/auth/registered-establishments/export")
public class RegisteredEstablishmentExportJobController {

	private static final Logger logger = LoggerFactory.getLogger(RegisteredEstablishmentExportJobController.class);

	private final RegisteredEstablishmentExportJobFacade registeredEstablishmentExportJobFacade;

	public RegisteredEstablishmentExportJobController(
			RegisteredEstablishmentExportJobFacade registeredEstablishmentExportJobFacade) {
		this.registeredEstablishmentExportJobFacade = registeredEstablishmentExportJobFacade;
	}

	@PostMapping("/jobs")
	public ResponseEntity<RegisteredEstablishmentExportJobCreatedResponse> createExportJob(
			@RequestBody RegisteredEstablishmentExportJobRequest request) {
		RegisteredEstablishmentExportFormat format = RegisteredEstablishmentExportFormat.fromValue(request.format());
		RegisteredEstablishmentExportJobCreatedResponse response = registeredEstablishmentExportJobFacade.startExportJob(
				format, request.districtIds(), request.talukaIds(), request.brn());
		logger.info("Created registered establishments export job. jobId={} format={}", response.jobId(),
				response.format());
		return ResponseEntity.accepted().body(response);
	}

	@GetMapping("/jobs/{jobId}")
	public ResponseEntity<RegisteredEstablishmentExportJobStatusResponse> getExportJobStatus(
			@PathVariable String jobId) {
		return ResponseEntity.ok(registeredEstablishmentExportJobFacade.getExportJobStatus(jobId));
	}

	@GetMapping("/jobs/{jobId}/download")
	public ResponseEntity<InputStreamResource> downloadExport(@PathVariable String jobId) throws IOException {
		RegisteredEstablishmentExportDownload download = registeredEstablishmentExportJobFacade.getExportDownload(jobId);

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(download.mediaType()))
				.contentLength(download.fileSize())
				.header(HttpHeaders.CONTENT_DISPOSITION,
						ContentDisposition.attachment().filename(download.fileName()).build().toString())
				.body(new InputStreamResource(java.nio.file.Files.newInputStream(download.path())));
	}
}
