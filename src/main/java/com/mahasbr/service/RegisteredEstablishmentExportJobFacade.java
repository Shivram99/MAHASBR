package com.mahasbr.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mahasbr.dto.RegisteredEstablishmentExportJobCreatedResponse;
import com.mahasbr.dto.RegisteredEstablishmentExportJobStatusResponse;

@Service
public class RegisteredEstablishmentExportJobFacade {

	private final RegisteredEstablishmentExportService registeredEstablishmentExportService;
	private final RegisteredEstablishmentExportJobRunner registeredEstablishmentExportJobRunner;

	public RegisteredEstablishmentExportJobFacade(
			RegisteredEstablishmentExportService registeredEstablishmentExportService,
			RegisteredEstablishmentExportJobRunner registeredEstablishmentExportJobRunner) {
		this.registeredEstablishmentExportService = registeredEstablishmentExportService;
		this.registeredEstablishmentExportJobRunner = registeredEstablishmentExportJobRunner;
	}

	public RegisteredEstablishmentExportJobCreatedResponse startExportJob(RegisteredEstablishmentExportFormat format,
			List<Long> districtIds, List<Long> talukaIds, String brn) {
		RegisteredEstablishmentExportJobCreatedResponse response = registeredEstablishmentExportService.startExportJob(
				format, districtIds, talukaIds, brn);
		registeredEstablishmentExportJobRunner.runAsync(response.jobId());
		return response;
	}

	public RegisteredEstablishmentExportJobStatusResponse getExportJobStatus(String jobId) {
		return registeredEstablishmentExportService.getExportJobStatus(jobId);
	}

	public RegisteredEstablishmentExportDownload getExportDownload(String jobId) {
		return registeredEstablishmentExportService.getExportDownload(jobId);
	}
}
